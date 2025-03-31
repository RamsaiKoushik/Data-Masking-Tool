package datamaskingtool.maskingStrategiesFactories;

import datamaskingtool.maskingStrategies.MaskingStrategy;
import datamaskingtool.maskingStrategies.RedactionStrategy;

public class RedactionStrategyFactory implements MaskingStrategyFactory {

    private boolean fullRedaction;

    public RedactionStrategyFactory(boolean fullRedaction){
        this.fullRedaction=fullRedaction;
    }

    public MaskingStrategy createStrategy() {
        return new RedactionStrategy(fullRedaction);
    }
}
