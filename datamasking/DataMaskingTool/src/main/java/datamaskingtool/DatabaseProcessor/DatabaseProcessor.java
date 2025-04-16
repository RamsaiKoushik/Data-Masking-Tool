package datamaskingtool.DatabaseProcessor;
import java.sql.*;
import java.util.*;
import java.sql.Date;
import datamaskingtool.CustomClasses.*;
import datamaskingtool.DataClasses.*;
import datamaskingtool.maskingStrategies.MaskingStrategy;

public class DatabaseProcessor {
    private Database database;
    private String DB_URL;
    private String USER;
    private String PASSWORD;
    private String NEW_DB_NAME;
    private String OLD_DB_NAME;

    public DatabaseProcessor(Database database, String DB_URL, String NEW_DB_NAME){
        this.database = database;
        this.DB_URL = DB_URL;
        this.USER = database.getUsername();
        this.PASSWORD = database.getPassword();
        this.OLD_DB_NAME = database.getDbName();
        this.NEW_DB_NAME = NEW_DB_NAME;
    }

    public void ReplicateDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Step 1: Drop and recreate new database
            stmt.executeUpdate("DROP DATABASE IF EXISTS " + NEW_DB_NAME);
            stmt.executeUpdate("CREATE DATABASE " + NEW_DB_NAME);

            // Step 2: Get all tables from the old database
            stmt.executeUpdate("USE " + OLD_DB_NAME);
            ResultSet rsTables = stmt.executeQuery("SHOW TABLES");

            List<String> tables = new ArrayList<>();
            while (rsTables.next()) {
                tables.add(rsTables.getString(1));
            }

            // Step 3: Switch to new database
            stmt.executeUpdate("USE " + NEW_DB_NAME);

            for (String table : tables) {
                // Get CREATE TABLE statement from old database
                ResultSet rsCreate = stmt.executeQuery("SHOW CREATE TABLE " + OLD_DB_NAME + "." + table);
                if (rsCreate.next()) {
                    String createStmt = rsCreate.getString(2);

                    // Remove constraints and foreign keys
                    createStmt = createStmt.replaceAll(",\\s*CONSTRAINT.*?\\)", "")
                            .replaceAll(",\\s*FOREIGN KEY.*?\\)", "")
                            .replaceAll("REFERENCES\\s+[^\\s]+\\s*\\([^\\)]*\\)", "")
                            .replaceAll("NOT NULL", "")
                            .replaceAll("DEFAULT\\s+[^,\\s)]+", "")
                            .replaceAll("AUTO_INCREMENT", "")
                            .replaceAll("\\s+UNSIGNED", "");

                    // Add new column for row number at the end before final closing parenthesis
                    int lastParenIndex = createStmt.lastIndexOf(")");
                    if (lastParenIndex != -1) {
                        createStmt = createStmt.substring(0, lastParenIndex) +
                                ", row_num INT" + createStmt.substring(lastParenIndex);
                    }

                    stmt.executeUpdate(createStmt); // Create modified table in new DB
                }

                // Copy data from old to new with row numbers
                String insertStmt = "INSERT INTO " + NEW_DB_NAME + "." + table +
                        " SELECT t.* FROM (" +
                        " SELECT *, ROW_NUMBER() OVER () AS row_num FROM " + OLD_DB_NAME + "." + table +
                        ") AS t";

                System.out.println(insertStmt);
                stmt.executeUpdate(insertStmt);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void processDatabase(List<String> sortedColumns) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Step 1: Drop database if it exists and create a new one
//            stmt.executeUpdate("DROP DATABASE IF EXISTS " + NEW_DB_NAME);
//            stmt.executeUpdate("CREATE DATABASE " + NEW_DB_NAME);

            ReplicateDatabase();
            stmt.executeUpdate("USE " + NEW_DB_NAME);


            for (String entry : sortedColumns) {
                String[] parts = entry.split("\\.");
                String tableName = parts[0];
                String columnName = parts[1];

                Table table = getTableByName(database, tableName);
                if (table == null) continue;

                if (Objects.equals(table.getTo_mask(), "NO")){
                    continue;
                }

                Column column = getColumnByName(table, columnName);
                if (column == null) continue;

                // Step 4: Retrieve and output the masking strategy
                String maskingStrategy = column.getMaskingStrategy();
                System.out.println("Column: " + columnName + " | Masking Strategy: " + maskingStrategy);

                if (Objects.equals(maskingStrategy, "no_masking")){
                    continue;
                }
                MaskingStrategyManager msm = new MaskingStrategyManager(maskingStrategy);
                MaskingStrategy strategy = msm.getStrategy();

                // Step 5: Query the original database for column values
                ListObjectWithDataType values = fetchColumnValues(tableName, columnName);
                int columnType = values.getColumnType();

                switch (columnType) {
                    case Types.INTEGER:
                        List<Integer> integerList = values.getList().stream()
                                .filter(obj -> obj instanceof Number) // Ensure it's a number
                                .map(obj -> ((Number) obj).intValue()) // Convert to Integer
                                .toList();
                        CustomIntegerList customIntegerList = new CustomIntegerList(integerList);

                        try (PreparedStatement pstmt = conn.prepareStatement(
                                     "UPDATE " + table + " SET " + column + " = ? WHERE row_num = ?")) {

                            for (int i = 0; i < customIntegerList.size(); i++) {
                                pstmt.setInt(1, customIntegerList.get(i)); // value
                                pstmt.setInt(2, i + 1); // row_num is list index + 1
                                pstmt.executeUpdate();
                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        break;
                    case Types.FLOAT:
                    case Types.DOUBLE:
                    case Types.REAL:
                        List<Float> floatList = values.getList().stream()
                                .filter(obj -> obj instanceof Number)
                                .map(obj -> ((Number) obj).floatValue()) // Convert to Float
                                .toList();
                        CustomFloatList customFloatList = new CustomFloatList(floatList);

                        try (PreparedStatement pstmt = conn.prepareStatement(
                                "UPDATE " + table + " SET " + column + " = ? WHERE row_num = ?")) {

                            for (int i = 0; i < customFloatList.size(); i++) {
                                pstmt.setFloat(1, customFloatList.get(i)); // value
                                pstmt.setInt(2, i + 1); // row_num is list index + 1
                                pstmt.executeUpdate();
                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        break;
                    case Types.BOOLEAN:
                        List<Boolean> booleanList = values.getList().stream()
                                .map(obj -> {
                                    if (obj instanceof Boolean) return (Boolean) obj;
                                    if (obj instanceof String) return Boolean.parseBoolean(((String) obj).toLowerCase());
                                    return null; // Ignore unsupported types
                                })
                                .filter(Objects::nonNull)
                                .toList();
                        CustomBooleanList customBooleanList = new CustomBooleanList(booleanList);

                        try (PreparedStatement pstmt = conn.prepareStatement(
                                "UPDATE " + table + " SET " + column + " = ? WHERE row_num = ?")) {

                            for (int i = 0; i < customBooleanList.size(); i++) {
                                pstmt.setBoolean(1, customBooleanList.get(i)); // value
                                pstmt.setInt(2, i + 1); // row_num is list index + 1
                                pstmt.executeUpdate();
                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        break;
                    case Types.DATE:
                        List<Date> dateList = values.getList().stream()
                                .map(obj -> {
                                    try {
                                        if (obj instanceof Date) return (Date) obj;
                                        if (obj instanceof String) return Date.valueOf((String) obj);
                                        if (obj instanceof Number) return new Date(((Number) obj).longValue());
                                    } catch (Exception ignored) {}
                                    return null;
                                })
                                .filter(Objects::nonNull)
                                .toList();
                        CustomDateList customDateList = new CustomDateList(dateList);

                        try (PreparedStatement pstmt = conn.prepareStatement(
                                "UPDATE " + table + " SET " + column + " = ? WHERE row_num = ?")) {

                            for (int i = 0; i < customDateList.size(); i++) {
                                pstmt.setDate(1, customDateList.get(i)); // value
                                pstmt.setInt(2, i + 1); // row_num is list index + 1
                                pstmt.executeUpdate();
                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        break;
//                    case Types.TIMESTAMP:
//                        List<Timestamp> timestampList = values.getList().stream()
//                                .map(obj -> {
//                                    try {
//                                        if (obj instanceof Timestamp) return (Timestamp) obj;
//                                        if (obj instanceof String) return Timestamp.valueOf((String) obj);
//                                        if (obj instanceof Number) return new Timestamp(((Number) obj).longValue());
//                                    } catch (Exception ignored) {}
//                                    return null;
//                                })
//                                .filter(Objects::nonNull)
//                                .toList();
//
//                        break;
                    case Types.VARCHAR:
                    case Types.CHAR:
                    case Types.LONGVARCHAR:
                        List<String> stringList = values.getList().stream()
                                .map(String::valueOf) // Converts everything to String
                                .toList();
                        CustomStringList customStringList = new CustomStringList();

                        try (PreparedStatement pstmt = conn.prepareStatement(
                                "UPDATE " + table + " SET " + column + " = ? WHERE row_num = ?")) {

                            for (int i = 0; i < customStringList.size(); i++) {
                                pstmt.setString(1, customStringList.get(i)); // value
                                pstmt.setInt(2, i + 1); // row_num is list index + 1
                                pstmt.executeUpdate();
                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ;
    }

    private Table getTableByName(Database database, String tableName) {
        for (Table table : database.getTables()) {
            if (table.getTableName().equals(tableName)) {
                return table;
            }
        }
        return null;
    }

    private Column getColumnByName(Table table, String columnName) {
        for (Column column : table.getColumns()) {
            if (column.getColumnName().equals(columnName)) {
                return column;
            }
        }
        return null;
    }

    private void createNewTable(Statement stmt, Table table) throws SQLException {
        StringBuilder createQuery = new StringBuilder("CREATE TABLE " + table.getTableName() + " (");

        List<String> columnDefinitions = new ArrayList<>();
        for (Column column : table.getColumns()) {
            String dataType = column.getDataType();
            String size = column.getColumn_size();

            // Append size only if the data type is CHAR or VARCHAR
            if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("VARCHAR")) {
                columnDefinitions.add(column.getColumnName() + " " + dataType + "(" + size + ")");
            } else {
                columnDefinitions.add(column.getColumnName() + " " + dataType);
            }
        }

        createQuery.append(String.join(", ", columnDefinitions)).append(");");
        System.out.println(createQuery);
        stmt.executeUpdate(createQuery.toString());
        System.out.println("Created Table: " + table.getTableName());
    }

    private ListObjectWithDataType fetchColumnValues(String tableName, String columnName) {
        String query = "SELECT " + columnName + " FROM " + tableName;
        try (Connection conn = DriverManager.getConnection(DB_URL + OLD_DB_NAME, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            List<Object> values = new ArrayList<>();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnType = metaData.getColumnType(1);

            while (rs.next()) {
                switch (columnType) {
                    case Types.INTEGER:
                        values.add(rs.getInt(columnName));
                        break;
                    case Types.FLOAT:
                    case Types.DOUBLE:
                    case Types.REAL:
                        values.add(rs.getDouble(columnName)); // Use double to cover all float types
                        break;
                    case Types.BOOLEAN:
                        values.add(rs.getBoolean(columnName));
                        break;
                    case Types.DATE:
                        values.add(rs.getDate(columnName));
                        break;
//                    case Types.TIMESTAMP:
//                        values.add(rs.getTimestamp(columnName));
//                        break;
                    case Types.VARCHAR:
                    case Types.CHAR:
                    case Types.LONGVARCHAR:
                        values.add(rs.getString(columnName));
                        break;
                    default:
                        values.add(rs.getObject(columnName)); // Fallback for unknown types
                }
            }

            System.out.println("Values for " + tableName + "." + columnName + ": " + values);
            return new ListObjectWithDataType(values, columnType);

        } catch (SQLException e) {
            System.out.println("Error retrieving values for " + tableName + "." + columnName);
            e.printStackTrace();
            return new ListObjectWithDataType(new ArrayList<>(), -1);
        }
    }
}