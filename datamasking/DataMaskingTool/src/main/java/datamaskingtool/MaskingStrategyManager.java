package datamaskingtool;

import datamaskingtool.maskingStrategies.MaskingStrategy;
import datamaskingtool.maskingStrategiesFactories.MaskingStrategyFactory;
import datamaskingtool.maskingStrategiesFactories.ShufflingStrategyFactory;

public class MaskingStrategyManager{
    private MaskingStrategy mStrategy;

    public MaskingStrategyManager(String strategy) {
       MaskingStrategyFactory factory;
       
        if ("Shuffle".equals(strategy)) {
            factory = new ShufflingStrategyFactory();
        } else {
            throw new IllegalArgumentException("Unknown masking strategy: " + strategy);
        }
        
        mStrategy = factory.createStrategy();
    }

    public MaskingStrategy getStrategy() {
        return mStrategy;
    }
}