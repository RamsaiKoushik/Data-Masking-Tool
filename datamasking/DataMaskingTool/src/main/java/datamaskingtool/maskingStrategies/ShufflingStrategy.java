package datamaskingtool.maskingStrategies;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import datamaskingtool.CustomClasses.CustomBooleanList;
import datamaskingtool.CustomClasses.CustomDateList;
import datamaskingtool.CustomClasses.CustomFloatList;
import datamaskingtool.CustomClasses.CustomIntegerList;
import datamaskingtool.CustomClasses.CustomStringList;

public class ShufflingStrategy extends MaskingStrategy{
    public CustomStringList mask(CustomStringList values){

        if (values == null || values.isEmpty()) {
            return values; 
        }

        CustomStringList shuffledList = new CustomStringList();
        shuffledList.addAll(values);
        Collections.shuffle(shuffledList);
    
        return shuffledList;
    }

    public CustomIntegerList mask(CustomIntegerList values){
        if (values == null || values.isEmpty()) {
            return values; 
        }

        CustomIntegerList shuffledList = new CustomIntegerList();
        shuffledList.addAll(values);
        Collections.shuffle(shuffledList); 

        return shuffledList;
    }

    public CustomFloatList mask(CustomFloatList values){
        if (values == null || values.isEmpty()) {
            return values; 
        }

        CustomFloatList shuffledList = new CustomFloatList();
        shuffledList.addAll(values);
        Collections.shuffle(shuffledList); 

        return shuffledList;
    }
    
    public CustomDateList mask(CustomDateList values){
        if (values == null || values.isEmpty()) {
            return values; 
        }

        CustomDateList shuffledList = new CustomDateList();
        shuffledList.addAll(values);
        Collections.shuffle(shuffledList);

        return shuffledList;
    }

    public CustomBooleanList mask(CustomBooleanList values){
        if (values == null || values.isEmpty()) {
            return values; 
        }

        CustomBooleanList shuffledList = new CustomBooleanList();
        shuffledList.addAll(values);
        Collections.shuffle(shuffledList);

        return shuffledList;
    }
}
