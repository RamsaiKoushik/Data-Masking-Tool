package datamaskingtool.maskingStrategies;

import datamaskingtool.CustomClasses.CustomBooleanList;
import datamaskingtool.CustomClasses.CustomDateList;
import datamaskingtool.CustomClasses.CustomFloatList;
import datamaskingtool.CustomClasses.CustomIntegerList;
import datamaskingtool.CustomClasses.CustomStringList;

public abstract class MaskingStrategy {
    public abstract CustomStringList mask(CustomStringList value);
    public abstract CustomIntegerList mask(CustomIntegerList value);
    public abstract CustomFloatList mask(CustomFloatList value);
    public abstract CustomDateList mask(CustomDateList value);
    public abstract CustomBooleanList mask(CustomBooleanList value);
}
