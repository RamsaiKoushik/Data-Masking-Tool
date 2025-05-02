package datamaskingtool.DatabaseProcessor;
import java.io.File;
import java.io.IOException;

public class DatabaseDumper {
    public static boolean dumpDatabase(String host, String port, String user, String password, String dbName, String outputPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "mysqldump",
                    "-h", host,
                    "-P", port,
                    "-u", user,
                    dbName
            );

            pb.environment().put("MYSQL_PWD", password);

            pb.redirectOutput(new File(outputPath));
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean restoreDatabase(String host, String port, String user, String password, String dbName, String sqlFilePath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "mysql",
                    "-h", host,
                    "-P", port,
                    "-u", user,
                    dbName,
                    "-e", "source " + sqlFilePath
            );

            pb.environment().put("MYSQL_PWD", password);

//            pb.redirectInput(new File(sqlFilePath));
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();
            System.out.println(exitCode);

            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
