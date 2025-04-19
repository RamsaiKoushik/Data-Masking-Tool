package datamaskingtool.Controller;

import datamaskingtool.DataClasses.Database;
import datamaskingtool.DataClasses.XMLParser;
import datamaskingtool.DatabaseProcessor.DatabaseProcessor;
import datamaskingtool.TopologicalSort.DatabaseTopologicalSort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
public class DataMaskingController {

    @PostMapping(value = "/ProcessData", consumes = MediaType.APPLICATION_XML_VALUE)
    public void getSchema(@RequestBody String xmlContent) {
        Database database = XMLParser.parse_xml(xmlContent);
        List<String> columns = DatabaseTopologicalSort.topologicalSort(database);
        for (String column: columns){
            System.out.println(column);
        }
        DatabaseProcessor databaseProcessor = new DatabaseProcessor(database);
        databaseProcessor.processDatabase(columns);
    }
}
