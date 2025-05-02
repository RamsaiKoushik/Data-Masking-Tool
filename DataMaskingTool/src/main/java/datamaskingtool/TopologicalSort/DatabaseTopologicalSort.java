package datamaskingtool.TopologicalSort;

import java.util.*;
import datamaskingtool.DataClasses.*;

public class DatabaseTopologicalSort {
    public static List<String> topologicalSort(Database database) {

        Map<String, List<String>> graph = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();

        for (Table table : database.getTables()) {
            String tableName = table.getTableName();
            List<String> tablePrimaryKeys = new ArrayList<>();
            List<String> tableNonPrimaryKeys = new ArrayList<>();

            for (Column column : table.getColumns()) {
                String node = tableName + "." + column.getColumnName();
                graph.put(node, new ArrayList<>());
                inDegree.put(node, 0);

                if (table.getPrimaryKeys().contains(column.getColumnName())) {
                    tablePrimaryKeys.add(node);
                } else {
                    tableNonPrimaryKeys.add(node);
                }
            }
            for (String pk : tablePrimaryKeys) {
                for (String nonPk : tableNonPrimaryKeys) {
                    graph.get(pk).add(nonPk);
                    inDegree.put(nonPk, inDegree.get(nonPk) + 1);
                }
            }
        }

        for (Table table : database.getTables()) {
            String fromTable = table.getTableName();
            if (table.getForeignKeys() != null) {
                for (ForeignKey fk : table.getForeignKeys()) {
                    String toTable = fk.getForeignTable(); // Table being referenced
                    String fromColumn = fromTable + "." + fk.getColumnName();
                    String toColumn = toTable + "." + fk.getForeignColumn();

                    graph.get(toColumn).add(fromColumn); // Correct edge direction
                    inDegree.put(fromColumn, inDegree.get(fromColumn) + 1);
                }
            }
        }

        Queue<String> queue = new LinkedList<>();
        List<String> sortedOrder = new ArrayList<>();

        for (String column : inDegree.keySet()) {
            if (inDegree.get(column) == 0) {
                queue.add(column);
            }
        }

        while (!queue.isEmpty()) {
            String current = queue.poll();
            sortedOrder.add(current);

            for (String neighbor : graph.get(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (sortedOrder.size() != inDegree.size()) {
            throw new RuntimeException("Cycle detected! Topological sorting at column level is not possible.");
        }

        return sortedOrder;
    }
}
