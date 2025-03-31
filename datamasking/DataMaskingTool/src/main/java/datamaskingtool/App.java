package datamaskingtool;
import datamaskingtool.DataClasses.XMLParser;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        XMLParser xmlparser = new XMLParser();
        XMLParser.parse_xml("src/main/java/datamaskingtool/config/config.xml");
    }
}
