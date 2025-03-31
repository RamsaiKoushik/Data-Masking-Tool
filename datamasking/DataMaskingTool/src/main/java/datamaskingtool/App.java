package datamaskingtool;
import datamaskingtool.DataClasses.XMLParser;
import datamaskingtool.DataClasses.Database;
import datamaskingtool.TopologicalSort.DatabaseTopologicalSort;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Database database = XMLParser.parse_xml("src/main/java/datamaskingtool/config/config.xml");
        List<String> columns = DatabaseTopologicalSort.topologicalSort(database);
        for (String column: columns){
            System.out.println(column);
        }
    }
}
