package datamaskingtool.DataClasses;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "database")
@XmlAccessorType(XmlAccessType.FIELD)
public class Database {
    private String db_name;
    private String username;
    private String password;

    @XmlElementWrapper(name = "tables")
    @XmlElement(name = "table")
    private List<Table> tables;

    // Getters and Setters
    public String getDbName() { return db_name; }
    public void setDbName(String db_name) { this.db_name = db_name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<Table> getTables() { return tables; }
    public void setTables(List<Table> tables) { this.tables = tables; }
}
