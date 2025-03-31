package datamaskingtool.maskingStrategies;

import java.util.Collections;
import java.util.List;

import main.java.datamaskingtool.CustomClasses.CustomFloatList;
import main.java.datamaskingtool.CustomClasses.CustomIntegerList;
import main.java.datamaskingtool.CustomClasses.CustomStringList;

public class ShufflingStrategy extends MaskingStrategy{
    public CustomStringList mask(CustomStringList values){

        if (values == null || values.isEmpty()) {
            return values; // Return as-is if null or empty
        }

        CustomStringList shuffledList = new CustomStringList();
        shuffledList.addAll(values);
        Collections.shuffle(shuffledList);// Create an immutable copy

        return shuffledList;
    }

    public CustomIntegerList mask(CustomIntegerList values){
        if (values == null || values.isEmpty()) {
            return values; // Return as-is if null or empty
        }

        CustomIntegerList shuffledList = new CustomIntegerList();
        shuffledList.addAll(values);
        Collections.shuffle(shuffledList); // Shuffle the copy

        return shuffledList;
    }

    public CustomFloatList mask(CustomFloatList values){
        if (values == null || values.isEmpty()) {
            return values; // Return as-is if null or empty
        }

        CustomFloatList shuffledList = new CustomFloatList();
        shuffledList.addAll(values);
        Collections.shuffle(shuffledList); // Shuffle the copy

        return shuffledList;
    }
}
