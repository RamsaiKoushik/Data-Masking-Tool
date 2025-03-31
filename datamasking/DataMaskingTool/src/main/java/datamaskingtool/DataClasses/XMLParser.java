package datamaskingtool.DataClasses;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class XMLParser {
    public static Database parse_xml(String filename) {
        try {
            // Load the XML file
            File file = new File(filename);

            // Create JAXB Context
            JAXBContext jaxbContext = JAXBContext.newInstance(Database.class);

            // Create Unmarshaller
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            // Convert XML to Java object
            Database database = (Database) jaxbUnmarshaller.unmarshal(file);

            Set<String> foreignKeyColumns = new HashSet<>();
            for (Table table : database.getTables()) {
                if (table.getForeignKeys() != null) {
                    for (ForeignKey fk : table.getForeignKeys()) {
                        foreignKeyColumns.add(table.getTableName() + "." + fk.getColumnName());
                    }
                }
            }

            // Modify masking strategy only for matching (table_name, column_name) pairs
            for (Table table : database.getTables()) {
                for (Column column : table.getColumns()) {
                    String key = table.getTableName() + "." + column.getColumnName();
                    if (foreignKeyColumns.contains(key)) {
                        column.setMaskingStrategy("LookupSubstitution");
                    }
                }
            }

//           System.out.println("Database Name: " + database.getDbName());
//           System.out.println("Username: " + database.getUsername());
//           System.out.println("Password: " + database.getPassword());
//
//           for (Table table : database.getTables()) {
//               System.out.println("\nTable: " + table.getTableName());
//
//               for (Column col : table.getColumns()) {
//                   System.out.println("  Column: " + col.getColumnName() +
//                           " (" + col.getDataType() + ", Nullable: " + col.getNullable() + ", Masking Strategy: " + col.getMaskingStrategy() + ")");
//               }
//
//               System.out.println("  Primary Keys: " + table.getPrimaryKeys());
//
//               for (ForeignKey fk : table.getForeignKeys()) {
//                   System.out.println("  Foreign Key: " + fk.getColumnName() +
//                           " -> " + fk.getForeignTable() + "." + fk.getForeignColumn());
//               }
//           }

           return database;

            
        } catch (JAXBException e) {
            e.printStackTrace();
            return new Database();
        }
    }
}
