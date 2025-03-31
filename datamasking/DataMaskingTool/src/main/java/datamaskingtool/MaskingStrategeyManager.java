package datamaskingtool;

import datamaskingtool.maskingStrategies.MaskingStrategy;
import datamaskingtool.maskingStrategiesFactories.MaskingStrategyFactory;

public class MaskingStrategeyManager{
    private MaskingStrategy mStrategy;

    public MaskingStrategeyManager(MaskingStrategyFactory factory) {
       mStrategy = factory.createStrategy();
    }

    public MaskingStrategy getStrategy() {
        return mStrategy;
    }
}