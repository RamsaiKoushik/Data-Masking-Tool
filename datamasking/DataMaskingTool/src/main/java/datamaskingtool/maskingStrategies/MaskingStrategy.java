package datamaskingtool.maskingStrategies;

import java.util.List;

import main.java.datamaskingtool.CustomClasses.CustomFloatList;
import main.java.datamaskingtool.CustomClasses.CustomIntegerList;
import main.java.datamaskingtool.CustomClasses.CustomStringList;

public abstract class MaskingStrategy {
    public abstract CustomStringList mask(CustomStringList value);
    public abstract CustomIntegerList mask(CustomIntegerList value);
    public abstract CustomFloatList mask(CustomFloatList value);
}
