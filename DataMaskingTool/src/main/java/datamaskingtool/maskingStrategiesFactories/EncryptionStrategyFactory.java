package datamaskingtool.maskingStrategiesFactories;

import datamaskingtool.maskingStrategies.EncryptionStrategy;
import datamaskingtool.maskingStrategies.MaskingStrategy;

public class EncryptionStrategyFactory implements MaskingStrategyFactory {
    public MaskingStrategy createStrategy() {
        return new EncryptionStrategy();
    }
}
