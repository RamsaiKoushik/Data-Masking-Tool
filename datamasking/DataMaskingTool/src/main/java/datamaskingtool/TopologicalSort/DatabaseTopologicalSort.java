package datamaskingtool.TopologicalSort;

import java.util.*;
import datamaskingtool.DataClasses.*;

public class DatabaseTopologicalSort {
    public static List<String> topologicalSort(Database database) {
        // Step 1: Build Graph
        Map<String, List<String>> graph = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();
        List<String> primaryKeys = new ArrayList<>();

        // Initialize graph with empty lists and zero in-degree for all columns
        for (Table table : database.getTables()) {
            String tableName = table.getTableName();
            for (Column column : table.getColumns()) {
                String node = tableName + "." + column.getColumnName();
                graph.put(node, new ArrayList<>());
                inDegree.put(node, 0);
            }
            // Store primary keys separately
            for (String pk : table.getPrimaryKeys()) {
                primaryKeys.add(tableName + "." + pk);
            }
        }

        // Step 2: Add edges based on foreign keys
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

        // Step 3: Perform Kahn's Algorithm (BFS-based Topological Sort)
        Queue<String> queue = new LinkedList<>();
        List<String> sortedOrder = new ArrayList<>();

        // Enqueue all columns with in-degree 0
        for (String column : inDegree.keySet()) {
            if (inDegree.get(column) == 0) {
                queue.add(column);
            }
        }

        // Process queue
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

        // Step 4: Check for cycles
        if (sortedOrder.size() != inDegree.size()) {
            throw new RuntimeException("Cycle detected! Topological sorting at column level is not possible.");
        }

        // Step 5: Reorder to place primary keys first while maintaining relative order
        List<String> finalOrder = new ArrayList<>();

        // Add primary keys first, in their original sorted order
        for (String column : sortedOrder) {
            if (primaryKeys.contains(column)) {
                finalOrder.add(column);
            }
        }

        // Add remaining columns while preserving order
        for (String column : sortedOrder) {
            if (!primaryKeys.contains(column)) {
                finalOrder.add(column);
            }
        }

        return finalOrder;
    }
}
