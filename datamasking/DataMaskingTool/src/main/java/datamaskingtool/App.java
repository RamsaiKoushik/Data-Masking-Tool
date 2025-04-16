package datamaskingtool;
import datamaskingtool.DataClasses.XMLParser;
import datamaskingtool.CustomClasses.CustomFloatList;
import datamaskingtool.CustomClasses.CustomStringList;
import datamaskingtool.DataClasses.Database;
import datamaskingtool.DatabaseProcessor.DatabaseProcessor;
import datamaskingtool.TopologicalSort.DatabaseTopologicalSort;
import datamaskingtool.DatabaseProcessor.MaskingStrategyManager;
import java.util.List;

public class App 
{
    public static void main( String[] args )
    {
        Database database = XMLParser.parse_xml("src/main/java/datamaskingtool/config/config.xml");
        List<String> columns = DatabaseTopologicalSort.topologicalSort(database);
        for (String column: columns){
            System.out.println(column);
        }

//        MaskingStrategyManager msm = new MaskingStrategyManager("Encryption");
//        CustomFloatList csl = new CustomFloatList();
//        csl.add(200.4f);
//        csl.add(443.4f); csl.add(3332.3f);
//        csl=msm.getStrategy().mask(csl);
//        for(Float s: csl){
//            System.out.println(s);
//        }

        DatabaseProcessor databaseProcessor = new DatabaseProcessor(database, "jdbc:mysql://localhost:3306/", "companydbnew");
        databaseProcessor.processDatabase(columns);

    }
}
