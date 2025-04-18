package datamaskingtool.DatabaseProcessor;

import datamaskingtool.maskingStrategies.MaskingStrategy;
import datamaskingtool.maskingStrategiesFactories.*;

public class MaskingStrategyManager{

    private MaskingStrategyFactory factory;

    public MaskingStrategy returnMaskingStrategy(String strategy, String tableName, String columnName){
        if ("Shuffle".equals(strategy)) {
            factory = new ShufflingStrategyFactory();
        } 
        else if("FullRedaction".equals(strategy)){
            factory = new RedactionStrategyFactory(true);
        }else if ("PartialRedaction".equals(strategy)){
            factory = new RedactionStrategyFactory(false);
        }
        else if("Encryption".equals(strategy)){
            factory = new EncryptionStrategyFactory();
        }
        else if ("LookupSubstitution".equals(strategy)){
            factory = new LookupSubstitutionStrategyFactory(tableName, columnName);
        }else if ("no_masking".equals(strategy)){
            factory = new NoMaskingStrategyFactory();
        }
        else {
            throw new IllegalArgumentException("Unknown masking strategy: " + strategy);
        }
        
        return factory.createStrategy();
    }
}