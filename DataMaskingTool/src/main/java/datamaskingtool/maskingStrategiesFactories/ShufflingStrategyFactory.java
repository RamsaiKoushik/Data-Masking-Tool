package datamaskingtool.maskingStrategiesFactories;

import datamaskingtool.maskingStrategies.MaskingStrategy;
import datamaskingtool.maskingStrategies.ShufflingStrategy;

public class ShufflingStrategyFactory implements MaskingStrategyFactory {
    public MaskingStrategy createStrategy() {
        return new ShufflingStrategy();
    }
}
