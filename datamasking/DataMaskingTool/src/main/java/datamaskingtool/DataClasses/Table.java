package datamaskingtool.DataClasses;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Table {
    private String table_name;

    private String to_mask;

    @XmlElementWrapper(name = "primary_keys")
    @XmlElement(name = "primary_key")
    private List<String> primaryKeys;

    @XmlElementWrapper(name = "unique_columns")
    @XmlElement(name = "unique_column")
    private List<String> uniqueColumns;

    @XmlElementWrapper(name = "columns")
    @XmlElement(name = "column")
    private List<Column> columns;

    @XmlElementWrapper(name = "foreign_keys")
    @XmlElement(name = "foreign_key")
    private List<ForeignKey> foreignKeys;

    // Getters and Setters
    public String getTableName() { return table_name; }
    public void setTableName(String table_name) { this.table_name = table_name; }

    public String getTo_mask() { return to_mask; }
    public void setTo_mask(String to_mask) { this.to_mask = to_mask; }

    public List<String> getPrimaryKeys() { return primaryKeys; }
    public void setPrimaryKeys(List<String> primaryKeys) { this.primaryKeys = primaryKeys; }

    public List<String> getUniqueColumns() { return uniqueColumns; }
    public void setUniqueColumns(List<String> uniqueColumns) { this.uniqueColumns = uniqueColumns; }

    public List<Column> getColumns() { return columns; }
    public void setColumns(List<Column> columns) { this.columns = columns; }

    public List<ForeignKey> getForeignKeys() { return foreignKeys; }
    public void setForeignKeys(List<ForeignKey> foreignKeys) { this.foreignKeys = foreignKeys; }
}
