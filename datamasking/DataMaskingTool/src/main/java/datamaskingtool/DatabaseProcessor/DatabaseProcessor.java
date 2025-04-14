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

    public void processDatabase(List<String> sortedColumns) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Step 1: Drop database if it exists and create a new one
            stmt.executeUpdate("DROP DATABASE IF EXISTS " + NEW_DB_NAME);
            stmt.executeUpdate("CREATE DATABASE " + NEW_DB_NAME);
            stmt.executeUpdate("USE " + NEW_DB_NAME);

            // Step 2: Iterate over sorted columns
            Set<String> createdTables = new HashSet<>();
            for (String entry : sortedColumns) {
                String[] parts = entry.split("\\.");
                String tableName = parts[0];
                String columnName = parts[1];

                Table table = getTableByName(database, tableName);
                if (table == null) continue;

                Column column = getColumnByName(table, columnName);
                if (column == null) continue;

                boolean isPrimaryKey = table.getPrimaryKeys().contains(columnName);

                // Step 3: If primary key, create table if not already created
                if (isPrimaryKey && !createdTables.contains(tableName)) {
                    createNewTable(stmt, table);
                    createdTables.add(tableName);
                }

                // Step 4: Retrieve and output the masking strategy
                String maskingStrategy = column.getMaskingStrategy();
                MaskingStrategyManager msm = new MaskingStrategyManager(maskingStrategy);
                MaskingStrategy strategy = msm.getStrategy();

                System.out.println("Column: " + columnName + " | Masking Strategy: " + maskingStrategy);

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
                        break;
                    case Types.FLOAT:
                    case Types.DOUBLE:
                    case Types.REAL:
                        List<Float> floatList = values.getList().stream()
                                .filter(obj -> obj instanceof Number)
                                .map(obj -> ((Number) obj).floatValue()) // Convert to Float
                                .toList();
                        CustomFloatList customFloatList = new CustomFloatList(floatList);
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