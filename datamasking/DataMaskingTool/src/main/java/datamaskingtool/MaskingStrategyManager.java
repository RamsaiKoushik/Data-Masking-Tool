package datamaskingtool;

import datamaskingtool.maskingStrategies.MaskingStrategy;
import datamaskingtool.maskingStrategiesFactories.EncryptionStrategyFactory;
import datamaskingtool.maskingStrategiesFactories.MaskingStrategyFactory;
import datamaskingtool.maskingStrategiesFactories.RedactionStrategyFactory;
import datamaskingtool.maskingStrategiesFactories.ShufflingStrategyFactory;

public class MaskingStrategyManager{
    private MaskingStrategy mStrategy;

    public MaskingStrategyManager(String strategy) {
       MaskingStrategyFactory factory;
       
        if ("Shuffle".equals(strategy)) {
            factory = new ShufflingStrategyFactory();
        } 
        else if("Redaction".equals(strategy)){
            boolean fullRedaction=false;
            factory=new RedactionStrategyFactory(fullRedaction);
        }
        else if("Encryption".equals(strategy)){
            factory=new EncryptionStrategyFactory();
        }
        else {
            throw new IllegalArgumentException("Unknown masking strategy: " + strategy);
        }
        
        mStrategy = factory.createStrategy();
    }

    public MaskingStrategy getStrategy() {
        return mStrategy;
    }
}