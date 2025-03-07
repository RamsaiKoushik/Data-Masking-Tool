package com.example.demo.datamasking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.*;

@RestController
public class SchemaController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/schema")
    public List<Map<String, Object>> getSchema() {
        List<Map<String, Object>> tables = new ArrayList<>();

        try {
            DatabaseMetaData metaData = jdbcTemplate.getDataSource().getConnection().getMetaData();
            String databaseName = jdbcTemplate.getDataSource().getConnection().getCatalog();

            ResultSet tablesResultSet = metaData.getTables(databaseName, null, "%", new String[]{"TABLE"});

            while (tablesResultSet.next()) {
                String tableName = tablesResultSet.getString("TABLE_NAME");
                Map<String, Object> tableInfo = new HashMap<>();
                tableInfo.put("table_name", tableName);

                List<Map<String, String>> columns = new ArrayList<>();
                ResultSet columnsResultSet = metaData.getColumns(databaseName, null, tableName, null);

                while (columnsResultSet.next()) {
                    Map<String, String> columnInfo = new HashMap<>();
                    columnInfo.put("column_name", columnsResultSet.getString("COLUMN_NAME"));
                    columnInfo.put("data_type", columnsResultSet.getString("TYPE_NAME"));
                    columnInfo.put("nullable", columnsResultSet.getString("IS_NULLABLE"));
                    columnInfo.put("default_value", columnsResultSet.getString("COLUMN_DEF"));
                    columnInfo.put("auto_increment", columnsResultSet.getString("IS_AUTOINCREMENT"));

                    columns.add(columnInfo);
                }

                // Fetch primary keys for the table
                List<String> primaryKeys = new ArrayList<>();
                ResultSet primaryKeySet = metaData.getPrimaryKeys(databaseName, null, tableName);
                while (primaryKeySet.next()) {
                    primaryKeys.add(primaryKeySet.getString("COLUMN_NAME"));
                }
                tableInfo.put("primary_keys", primaryKeys);

                // Fetch unique constraints for the table
                Set<String> uniqueColumns = new HashSet<>();
                ResultSet uniqueKeys = metaData.getIndexInfo(databaseName, null, tableName, true, false);
                while (uniqueKeys.next()) {
                    if (uniqueKeys.getString("COLUMN_NAME") != null) {
                        uniqueColumns.add(uniqueKeys.getString("COLUMN_NAME"));
                    }
                }
                tableInfo.put("unique_columns", uniqueColumns);

                // Fetch foreign keys for the table
                List<Map<String, String>> foreignKeys = new ArrayList<>();
                ResultSet foreignKeySet = metaData.getImportedKeys(databaseName, null, tableName);
                while (foreignKeySet.next()) {
                    Map<String, String> foreignKeyInfo = new HashMap<>();
                    foreignKeyInfo.put("column_name", foreignKeySet.getString("FKCOLUMN_NAME"));
                    foreignKeyInfo.put("foreign_table", foreignKeySet.getString("PKTABLE_NAME"));
                    foreignKeyInfo.put("foreign_column", foreignKeySet.getString("PKCOLUMN_NAME"));
                    foreignKeys.add(foreignKeyInfo);
                }
                tableInfo.put("foreign_keys", foreignKeys);

                tableInfo.put("columns", columns);
                tables.add(tableInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tables;
    }
}
