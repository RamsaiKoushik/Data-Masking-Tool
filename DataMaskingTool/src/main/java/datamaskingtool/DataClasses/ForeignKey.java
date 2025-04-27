package datamaskingtool.DataClasses;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class ForeignKey {
    private String foreign_table;
    private String column_name;
    private String foreign_column;

    // Getters and Setters
    public String getForeignTable() { return foreign_table; }
    public void setForeignTable(String foreign_table) { this.foreign_table = foreign_table; }

    public String getColumnName() { return column_name; }
    public void setColumnName(String column_name) { this.column_name = column_name; }

    public String getForeignColumn() { return foreign_column; }
    public void setForeignColumn(String foreign_column) { this.foreign_column = foreign_column; }
}
