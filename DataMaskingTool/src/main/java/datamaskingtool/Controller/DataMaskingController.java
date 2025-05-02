package datamaskingtool.Controller;

import datamaskingtool.DataClasses.Database;
import datamaskingtool.DataClasses.XMLParser;
import datamaskingtool.DatabaseProcessor.DatabaseDumper;
import datamaskingtool.DatabaseProcessor.DatabaseProcessor;
import datamaskingtool.TopologicalSort.DatabaseTopologicalSort;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
public class DataMaskingController {

    @PostMapping(value = "/ProcessDataDump", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Map<String, String>> getDataDump(@RequestBody String xmlContent) {
        Database database = XMLParser.parse_xml(xmlContent);
        String db_name_new = database.getDbName()+"new";

        List<String> columns = DatabaseTopologicalSort.topologicalSort(database);
        for (String column: columns){
            System.out.println(column);
        }

        String dumpFilename = UUID.randomUUID().toString();
        DatabaseProcessor databaseProcessor = new DatabaseProcessor(database, db_name_new);
        databaseProcessor.processDatabase(columns);
        DatabaseDumper.dumpDatabase("localhost", "3306", database.getUsername(), database.getPassword(), db_name_new, dumpFilename + ".sql");

        Map<String, String> response = new HashMap<>();
        response.put("filename", dumpFilename);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filename) throws IOException {
        Path filePath = Paths.get("./").resolve(filename+".sql");
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new FileNotFoundException("File not found " + filename);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping(value = "/ProcessData", consumes = MediaType.APPLICATION_XML_VALUE)
    // public void maskData(@RequestBody String xmlContent, @RequestParam String db_url, @RequestParam String db_name_new, @RequestParam String username, @RequestParam String password) {
    //     Database database = XMLParser.parse_xml(xmlContent);
    //     List<String> columns = DatabaseTopologicalSort.topologicalSort(database);
    //     for (String column: columns){
    //         System.out.println(column);
    //     }
    //     DatabaseProcessor databaseProcessor = new DatabaseProcessor(database, db_url, db_name_new, username, password);
    //     databaseProcessor.processDatabase(columns);
    // }
    public ResponseEntity<String> maskData(
        @RequestBody String xmlContent,
        @RequestParam String db_url,
        @RequestParam String db_name_new,
        @RequestParam String username,
        @RequestParam String password) {
        try {
            Database database = XMLParser.parse_xml(xmlContent);
            List<String> columns = DatabaseTopologicalSort.topologicalSort(database);
            for (String column : columns) {
                System.out.println(column);
            }
            DatabaseProcessor databaseProcessor = new DatabaseProcessor(database, db_url, db_name_new, username, password);
            databaseProcessor.processDatabase(columns);
            return ResponseEntity.ok("Data processed successfully");
        } catch (Exception e) {
            e.printStackTrace(); // Optional: log the error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to process data: " + e.getMessage());
        }
    }
}
