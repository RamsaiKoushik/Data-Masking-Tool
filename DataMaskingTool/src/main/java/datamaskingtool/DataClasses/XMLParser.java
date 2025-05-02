package datamaskingtool.DataClasses;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

public class XMLParser {
    public static Database parse_xml(String xmlContent) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Database.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            StringReader reader = new StringReader(xmlContent);
            Database database = (Database) jaxbUnmarshaller.unmarshal(reader);

            Set<String> foreignKeyColumns = new HashSet<>();
            for (Table table : database.getTables()) {
                if (table.getForeignKeys() != null) {
                    for (ForeignKey fk : table.getForeignKeys()) {
                        foreignKeyColumns.add(table.getTableName() + "." + fk.getColumnName());
                    }
                }
            }

            for (Table table : database.getTables()) {
                for (Column column : table.getColumns()) {
                    String key = table.getTableName() + "." + column.getColumnName();
                    if (foreignKeyColumns.contains(key)) {
                        column.setMaskingStrategy("LookupSubstitution");
                    }
                }
            }

            return database;

        } catch (JAXBException e) {
            e.printStackTrace();
            return new Database(); // or handle as needed
        }
    }
}
