package datamaskingtool.DatabaseProcessor;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

import datamaskingtool.CustomClasses.*;
import datamaskingtool.DataClasses.*;
import datamaskingtool.maskingStrategies.LookupSubstitutionStrategy;
import datamaskingtool.maskingStrategies.MaskingStrategy;

public class DatabaseProcessor {
    private Database database;
    private String DB_URL;
    private String USER;
    private String PASSWORD;
    private String OLD_DB_NAME;

    private String NEW_DB_URL;
    private String NEW_USER;
    private String NEW_PASSWORD;
    private String NEW_DB_NAME;

    public DatabaseProcessor(Database database, String db_new){
        this.database = database;
        this.DB_URL = database.getDb_url();
        this.USER = database.getUsername();
        this.PASSWORD = database.getPassword();
        this.OLD_DB_NAME = database.getDbName();
        this.NEW_DB_NAME = db_new;
        this.NEW_DB_URL = DB_URL;
        this.NEW_USER = USER;
        this.NEW_PASSWORD = PASSWORD;
    }

    public DatabaseProcessor(Database database, String newDbURL, String newDatabase, String newUser, String newPassword){
        this.database = database;
        this.DB_URL = database.getDb_url();
        this.USER = database.getUsername();
        this.PASSWORD = database.getPassword();
        this.OLD_DB_NAME = database.getDbName();
        this.NEW_DB_NAME = newDatabase;
        this.NEW_DB_URL = newDbURL;
        this.NEW_USER = newUser;
        this.NEW_PASSWORD = newPassword;
    }

    public void generateDump() throws URISyntaxException {
        URI uri = new URI(DB_URL.substring(5));
        String host = uri.getHost();
        int port = uri.getPort();
        DatabaseDumper.dumpDatabase(host, String.valueOf(port), USER, PASSWORD, OLD_DB_NAME, OLD_DB_NAME+".sql");
    }

    public void populateDatabase() throws URISyntaxException {
        URI uri = new URI(NEW_DB_URL.substring(5));
        String host = uri.getHost();
        int port = uri.getPort();
        DatabaseDumper.restoreDatabase(host, String.valueOf(port), NEW_USER, NEW_PASSWORD, NEW_DB_NAME, OLD_DB_NAME+".sql");
    }

    public void ReplicateDatabase() throws URISyntaxException {

        generateDump();
        try (Connection conn = DriverManager.getConnection(NEW_DB_URL, NEW_USER, NEW_PASSWORD);
             Statement stmt = conn.createStatement();
        ) {

            // Step 1: Drop and recreate new database
            stmt.executeUpdate("DROP DATABASE IF EXISTS " + NEW_DB_NAME);
            stmt.executeUpdate("CREATE DATABASE " + NEW_DB_NAME);
            populateDatabase();

            // Step 2: Get all tables from the old database
            stmt.executeUpdate("USE " + NEW_DB_NAME);

            List<String> tables = database.getTables().stream().map(Table::getTableName).toList();

            for (String table : tables) {
                String checkFKQuery = "SELECT CONSTRAINT_NAME " +
                        "FROM information_schema.TABLE_CONSTRAINTS " +
                        "WHERE TABLE_SCHEMA = '" + NEW_DB_NAME + "' " +
                        "AND TABLE_NAME = '" + table + "' " +
                        "AND CONSTRAINT_TYPE = 'FOREIGN KEY'";

                try (Statement readStmt = conn.createStatement();
                     ResultSet fkRs = readStmt.executeQuery(checkFKQuery)) {

                    while (fkRs.next()) {
                        String constraintName = fkRs.getString("CONSTRAINT_NAME");
                        String removeFKQuery = "ALTER TABLE " + NEW_DB_NAME + "." + table + " DROP FOREIGN KEY " + constraintName;

                        try (Statement writeStmt = conn.createStatement()) {
                            writeStmt.executeUpdate(removeFKQuery);
                        }
                    }
                }
            }

            for (String table : tables) {
                String checkPKQuery = "SELECT CONSTRAINT_NAME " +
                        "FROM information_schema.TABLE_CONSTRAINTS " +
                        "WHERE TABLE_SCHEMA = '" + NEW_DB_NAME + "' " +
                        "AND TABLE_NAME = '" + table + "' " +
                        "AND CONSTRAINT_TYPE = 'PRIMARY KEY'";

                try (Statement readStmt = conn.createStatement();
                     ResultSet pkRs = readStmt.executeQuery(checkPKQuery)) {

                    while (pkRs.next()) {
                        String constraintName = pkRs.getString("CONSTRAINT_NAME");
                        String removePKQuery = "ALTER TABLE " + NEW_DB_NAME + "." + table + " DROP PRIMARY KEY";

                        try (Statement writeStmt = conn.createStatement()) {
                            writeStmt.executeUpdate(removePKQuery);
                        }
                    }

                }
            }

            for (String table: tables){

                String addRowNumColumn = "ALTER TABLE " + NEW_DB_NAME + "." + table + " ADD COLUMN row_num INT";
                stmt.executeUpdate(addRowNumColumn);

                Table table_obj = getTableByName(database, table);
                assert table_obj != null;
                List<String> columns = table_obj.getColumns().stream().map(Column::getColumnName).toList();

                String joinCondition = columns.stream()
                        .map(col -> "(t." + col + " = n." + col + " OR (t." + col + " IS NULL AND n." + col + " IS NULL))")
                        .collect(Collectors.joining(" AND "));

                String updateRowNumQuery =
                        "WITH numbered AS (" +
                                "  SELECT " + String.join(", ", columns) + ", ROW_NUMBER() OVER () AS rn FROM " + NEW_DB_NAME + "." + table +
                                ") " +
                                "UPDATE " + NEW_DB_NAME + "." + table + " t " +
                                "JOIN numbered n ON " + joinCondition +
                                " SET t.row_num = n.rn";

                System.out.println(updateRowNumQuery);
                stmt.executeUpdate(updateRowNumQuery);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void processDatabase(List<String> sortedColumns) {
        try (Connection conn = DriverManager.getConnection(NEW_DB_URL, NEW_USER, NEW_PASSWORD);
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

                String maskingStrategy = column.getMaskingStrategy();
                System.out.println("Column: " + columnName + " | Masking Strategy: " + maskingStrategy);
                MaskingStrategy strategy = msm.returnMaskingStrategy(maskingStrategy, list.get(0), list.get(1));
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
                                if (obj instanceof String) return java.sql.Date.valueOf((String) obj);
                                if (obj instanceof Number) return new java.sql.Date(((Number) obj).longValue());
                            } catch (Exception ignored) {}
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .map(obj -> (Date) obj) // <-- This forces type inference
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
                        writeToDatabase(conn, maskedStringList, tableName, columnName);
                        updateLookupTables(isPrimaryKey, tableName, columnName, customStringList.getInternalList(), maskedStringList.getInternalList());
                        break;
                }
            }

            List<String> tables = database.getTables().stream().map(Table::getTableName).toList();

            for (String table: tables){
                String dropQuery = "ALTER TABLE " + NEW_DB_NAME + "." + table + " DROP COLUMN row_num";
                stmt.executeUpdate(dropQuery);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
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
        System.out.println(query);
        try (Connection conn = DriverManager.getConnection(NEW_DB_URL + NEW_DB_NAME, NEW_USER, NEW_PASSWORD);
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