package datamaskingtool.maskingStrategies;

import datamaskingtool.CustomClasses.*;

public class NoMaskingStrategy extends MaskingStrategy{
    @Override
    public CustomStringList mask(CustomStringList value) {
        return value;
    }

    @Override
    public CustomIntegerList mask(CustomIntegerList value) {
        return value;
    }

    @Override
    public CustomFloatList mask(CustomFloatList value) {
        return value;
    }

    @Override
    public CustomDateList mask(CustomDateList value) {
        return value;
    }

    @Override
    public CustomBooleanList mask(CustomBooleanList value) {
        return value;
    }
}
