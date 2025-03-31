package datamaskingtool.DataClasses;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Column {
    private String column_name;
    private String data_type;
    private String nullable;
    private String auto_increment;
    private String default_value;
    private String masking_strategy;

    // Getters and Setters
    public String getColumnName() { return column_name; }
    public void setColumnName(String column_name) { this.column_name = column_name; }

    public String getDataType() { return data_type; }
    public void setDataType(String data_type) { this.data_type = data_type; }

    public String getNullable() { return nullable; }
    public void setNullable(String nullable) { this.nullable = nullable; }

    public String getMaskingStrategy(){return masking_strategy;}
    public void setMaskingStrategy(String masking_strategy){this.masking_strategy = masking_strategy; }

    public String getAutoIncrement() { return auto_increment; }
    public void setAutoIncrement(String auto_increment) { this.auto_increment = auto_increment; }

    public String getDefaultValue() { return default_value; }
    public void setDefaultValue(String default_value) { this.default_value = default_value; }
}
