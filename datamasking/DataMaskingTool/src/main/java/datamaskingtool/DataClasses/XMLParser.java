package datamaskingtool.DataClasses;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;

public class XMLParser {
    public static void parse_xml(String filename) {
        try {
            // Load the XML file
            File file = new File(filename);

            // Create JAXB Context
            JAXBContext jaxbContext = JAXBContext.newInstance(Database.class);

            // Create Unmarshaller
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            // Convert XML to Java object
            Database database = (Database) jaxbUnmarshaller.unmarshal(file);

            // Print database details
            System.out.println("Database Name: " + database.getDbName());
            System.out.println("Username: " + database.getUsername());
            System.out.println("Password: " + database.getPassword());

            // Iterate over tables
            for (Table table : database.getTables()) {
                System.out.println("\nTable: " + table.getTableName());

                // Print columns
                for (Column col : table.getColumns()) {
                    System.out.println("  Column: " + col.getColumnName() +
                            " (" + col.getDataType() + ", Nullable: " + col.getNullable() + ", Masking Strategy: " + col.getMaskingStrategy() + ")");
                }

                // Print primary keys
                System.out.println("  Primary Keys: " + table.getPrimaryKeys());

                // Print foreign keys
                for (ForeignKey fk : table.getForeignKeys()) {
                    System.out.println("  Foreign Key: " + fk.getColumnName() +
                            " -> " + fk.getForeignTable() + "." + fk.getForeignColumn());
                }
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
