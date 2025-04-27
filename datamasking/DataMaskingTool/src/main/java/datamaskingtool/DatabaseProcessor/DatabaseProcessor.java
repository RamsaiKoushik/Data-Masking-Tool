package datamaskingtool.DatabaseProcessor;
import java.sql.*;
import java.util.*;
import java.sql.Date;
import datamaskingtool.CustomClasses.*;
import datamaskingtool.DataClasses.*;
import datamaskingtool.maskingStrategies.LookupSubstitutionStrategy;
import datamaskingtool.maskingStrategies.MaskingStrategy;

public class DatabaseProcessor {
    private Database database;
    private String DB_URL;
    private String USER;
    private String PASSWORD;
    private String NEW_DB_NAME;
    private String OLD_DB_NAME;

    public DatabaseProcessor(Database database){
        this.database = database;
        this.DB_URL = database.getDb_url();
        this.USER = database.getUsername();
        this.PASSWORD = database.getPassword();
        this.OLD_DB_NAME = database.getDbName();
        this.NEW_DB_NAME = database.getDbName()+"new";
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

            ReplicateDatabase();
            stmt.executeUpdate("USE " + NEW_DB_NAME);
            MaskingStrategyManager msm = new MaskingStrategyManager();

            for (String entry : sortedColumns) {
                String[] parts = entry.split("\\.");
                String tableName = parts[0];
                String columnName = parts[1];

                Table table = getTableByName(database, tableName);
                if (table == null) continue;

                Column column = getColumnByName(table, columnName);
                if (column == null) continue;

                List<String> list = table.getReferencedColumn(columnName);
                boolean isPrimaryKey = table.getPrimaryKeys().contains(columnName);

                // Step 4: Retrieve and output the masking strategy
                String maskingStrategy = column.getMaskingStrategy();
                System.out.println("Column: " + columnName + " | Masking Strategy: " + maskingStrategy);
                MaskingStrategy strategy = msm.returnMaskingStrategy(maskingStrategy, list.get(0), list.get(1));

                // Step 5: Query the original database for column values
                ListObjectWithDataType values = fetchColumnValues(tableName, columnName);
                int columnType = values.getColumnType();

                switch (columnType) {
                    case Types.TINYINT:
                    case Types.SMALLINT:
                    case Types.INTEGER :
                        List<Integer> integerList = values.getList().stream()
                                .filter(obj -> obj instanceof Number) // Ensure it's a number
                                .map(obj -> ((Number) obj).intValue()) // Convert to Integer
                                .toList();
                        CustomIntegerList customIntegerList = new CustomIntegerList(integerList);
                        CustomIntegerList maskedIntegerList =  strategy.mask(customIntegerList);
                        writeToDatabase(conn, maskedIntegerList, tableName, columnName);
                        updateLookupTables(isPrimaryKey, tableName, columnName, customIntegerList.getInternalList(), maskedIntegerList.getInternalList());

                        break;
                    case Types.FLOAT:
                    case Types.DOUBLE:
                    case Types.REAL:
                        List<Float> floatList = values.getList().stream()
                                .filter(obj -> obj instanceof Number)
                                .map(obj -> ((Number) obj).floatValue()) // Convert to Float
                                .toList();
                        CustomFloatList customFloatList = new CustomFloatList(floatList);
                        CustomFloatList maskedFloatList =  strategy.mask(customFloatList);
                        writeToDatabase(conn, maskedFloatList, tableName, columnName);
                        updateLookupTables(isPrimaryKey, tableName, columnName, customFloatList.getInternalList(), maskedFloatList.getInternalList());

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
                        CustomBooleanList maskedBooleanList =  strategy.mask(customBooleanList);
                        writeToDatabase(conn, maskedBooleanList, tableName, columnName);
                        updateLookupTables(isPrimaryKey, tableName, columnName, customBooleanList.getInternalList(), maskedBooleanList.getInternalList());

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
                        CustomDateList maskedDateList =  strategy.mask(customDateList);

                        writeToDatabase(conn, maskedDateList, tableName, columnName);
                        updateLookupTables(isPrimaryKey, tableName, columnName, customDateList.getInternalList(), maskedDateList.getInternalList());
                        break;
                    case Types.VARCHAR:
                    case Types.CHAR:
                    case Types.LONGVARCHAR:
                        List<String> stringList = values.getList().stream()
                                .map(String::valueOf) // Converts everything to String
                                .toList();
                        CustomStringList customStringList = new CustomStringList(stringList);
                        CustomStringList maskedStringList = strategy.mask(customStringList);

//                        for(String val: maskedStringList){
//                            System.out.print(val + ", ");
//                        }
//                        System.out.println("\n");
                        writeToDatabase(conn, maskedStringList, tableName, columnName);
                        updateLookupTables(isPrimaryKey, tableName, columnName, customStringList.getInternalList(), maskedStringList.getInternalList());
                        break;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    private void writeToDatabase(Connection conn, CustomList maskedList, String tableName, String columnName){
        try (PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE " + tableName + " SET " + columnName + " = ? WHERE row_num = ?")) {
            List<?> list = maskedList.getInternalList();
            for (int i = 0; i < list.size(); i++) {
                pstmt.setObject(1, list.get(i)); // value
                pstmt.setInt(2, i + 1); // row_num is list index + 1
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateLookupTables(boolean isPrimaryKey, String tableName, String columnName, List<?>original, List<?> updated){
        if (!isPrimaryKey) return;
        LookupSubstitutionStrategy.updateLookupTable(tableName, columnName, original, updated);
    }

    private ListObjectWithDataType fetchColumnValues(String tableName, String columnName) {
        String query = "SELECT " + columnName + " FROM " + tableName + " ORDER BY row_num" ;
        try (Connection conn = DriverManager.getConnection(DB_URL + NEW_DB_NAME, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            List<Object> values = new ArrayList<>();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnType = metaData.getColumnType(1);

            while (rs.next()) {
                values.add(rs.getObject(columnName));
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