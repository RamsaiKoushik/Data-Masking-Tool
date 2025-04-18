package datamaskingtool.maskingStrategiesFactories;

import datamaskingtool.maskingStrategies.LookupSubstitutionStrategy;
import datamaskingtool.maskingStrategies.MaskingStrategy;

public class LookupSubstitutionStrategyFactory implements MaskingStrategyFactory {

    private String tableName;
    private String columnName;

    public LookupSubstitutionStrategyFactory(String tableName, String columnName){
        this.tableName = tableName;
        this.columnName = columnName;
    }

    @Override
    public MaskingStrategy createStrategy() {
        return new LookupSubstitutionStrategy(tableName, columnName);
    }
}
