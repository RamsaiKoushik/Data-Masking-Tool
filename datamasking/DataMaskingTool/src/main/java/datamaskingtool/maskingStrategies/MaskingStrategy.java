package datamaskingtool.maskingStrategies;

import datamaskingtool.CustomClasses.CustomFloatList;
import datamaskingtool.CustomClasses.CustomIntegerList;
import datamaskingtool.CustomClasses.CustomStringList;

public abstract class MaskingStrategy {
    public abstract CustomStringList mask(CustomStringList value);
    public abstract CustomIntegerList mask(CustomIntegerList value);
    public abstract CustomFloatList mask(CustomFloatList value);
}
