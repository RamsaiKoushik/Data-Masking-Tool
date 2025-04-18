package datamaskingtool.maskingStrategies;

import datamaskingtool.CustomClasses.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Date;

public class LookupSubstitutionStrategy extends MaskingStrategy {

    private static Map<String, Map<Object, Object>> lookupTable = new HashMap<>();
    private String lookupKey;

    public LookupSubstitutionStrategy(String tableName, String columnName){
        this.lookupKey = tableName+"."+columnName;
    }

    public static void updateLookupTable(String tableName, String columnName, List<?> original, List<?> updated) {
        String key = tableName + "." + columnName;

        // Initialize the map for the key if not already present
        lookupTable.computeIfAbsent(key, k -> new HashMap<>());

        for (int i = 0; i < original.size(); i++) {
            Object o = original.get(i);
            Object u = updated.get(i);
            lookupTable.get(key).put(o, u);
        }
    }

    @Override
    public CustomStringList mask(CustomStringList values) {
        Map<Object, Object> lookup = lookupTable.get(lookupKey);
        CustomStringList maskedList = new CustomStringList();

        values.forEach(value -> maskedList.add((String) lookup.get(value)));
        return maskedList;
    }

    @Override
    public CustomIntegerList mask(CustomIntegerList values) {
        Map<Object, Object> lookup = lookupTable.get(lookupKey);
        CustomIntegerList maskedList = new CustomIntegerList();

        values.forEach(value -> maskedList.add((Integer) lookup.get(value)));
        return maskedList;
    }

    @Override
    public CustomFloatList mask(CustomFloatList values) {
        Map<Object, Object> lookup = lookupTable.get(lookupKey);
        CustomFloatList maskedList = new CustomFloatList();

        values.forEach(value -> maskedList.add((Float) lookup.get(value)));
        return maskedList;
    }

    @Override
    public CustomDateList mask(CustomDateList values) {
        Map<Object, Object> lookup = lookupTable.get(lookupKey);
        CustomDateList maskedList = new CustomDateList();

        values.forEach(value -> maskedList.add((Date) lookup.get(value)));
        return maskedList;
    }

    @Override
    public CustomBooleanList mask(CustomBooleanList values) {
        Map<Object, Object> lookup = lookupTable.get(lookupKey);
        CustomBooleanList maskedList = new CustomBooleanList();

        values.forEach(value -> maskedList.add((Boolean) lookup.get(value)));
        return maskedList;
    }
}
