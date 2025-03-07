package com.example.demo.datamasking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SchemaController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/schema")
    public List<Map<String, Object>> getSchema() {
        List<Map<String, Object>> tables = new ArrayList<>();

        try {
            DatabaseMetaData metaData = jdbcTemplate.getDataSource().getConnection().getMetaData();
            ResultSet tablesResultSet = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            while (tablesResultSet.next()) {
                String tableName = tablesResultSet.getString("TABLE_NAME");
                Map<String, Object> tableInfo = new HashMap<>();
                tableInfo.put("table_name", tableName);

                List<Map<String, String>> columns = new ArrayList<>();
                ResultSet columnsResultSet = metaData.getColumns(null, null, tableName, null);

                while (columnsResultSet.next()) {
                    Map<String, String> columnInfo = new HashMap<>();
                    columnInfo.put("column_name", columnsResultSet.getString("COLUMN_NAME"));
                    columnInfo.put("data_type", columnsResultSet.getString("TYPE_NAME"));
                    columns.add(columnInfo);
                }

                tableInfo.put("columns", columns);
                tables.add(tableInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tables;
    }
}
