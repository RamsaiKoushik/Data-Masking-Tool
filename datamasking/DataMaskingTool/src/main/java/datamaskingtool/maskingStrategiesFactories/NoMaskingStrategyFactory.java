package datamaskingtool.maskingStrategiesFactories;

import datamaskingtool.maskingStrategies.MaskingStrategy;
import datamaskingtool.maskingStrategies.NoMaskingStrategy;

public class NoMaskingStrategyFactory implements MaskingStrategyFactory{
    @Override
    public MaskingStrategy createStrategy() {
        return new NoMaskingStrategy();
    }
}
