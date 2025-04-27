package datamaskingtool.CustomClasses;

import java.util.List;

public class ListObjectWithDataType {

    private List<Object> list;
    private int columnType;

    public ListObjectWithDataType(List<Object> list, int columnType){
        this.list = list;
        this.columnType  = columnType;
    }

    public int getColumnType() {
        return columnType;
    }

    public List<Object> getList() {
        return list;
    }
}
