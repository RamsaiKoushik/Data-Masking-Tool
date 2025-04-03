package datamaskingtool.DatabaseProcessor;
import java.sql.*;
import java.util.*;
import datamaskingtool.DataClasses.*;

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
                System.out.println("Column: " + columnName + " | Masking Strategy: " + maskingStrategy);

                // Step 5: Query the original database for column values
                List<String> column_values = fetchColumnValues(tableName, columnName);

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

    private List<String> fetchColumnValues(String tableName, String columnName) {
        String query = "SELECT " + columnName + " FROM " + tableName;
        try (Connection conn = DriverManager.getConnection(DB_URL + OLD_DB_NAME, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            List<String> values = new ArrayList<>();
            while (rs.next()) {
                values.add(rs.getString(columnName));
            }
            System.out.println("Values for " + tableName + "." + columnName + ": " + values);

            return values;

        } catch (SQLException e) {
            System.out.println("Error retrieving values for " + tableName + "." + columnName);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}