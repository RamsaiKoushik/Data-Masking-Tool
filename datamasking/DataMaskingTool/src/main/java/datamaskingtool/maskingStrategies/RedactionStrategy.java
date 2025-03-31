package datamaskingtool.maskingStrategies;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import datamaskingtool.CustomClasses.CustomFloatList;
import datamaskingtool.CustomClasses.CustomIntegerList;
import datamaskingtool.CustomClasses.CustomStringList;
import datamaskingtool.maskingStrategies.Redaction.FloatRedactor;
import datamaskingtool.maskingStrategies.Redaction.IntegerRedactor;
import datamaskingtool.maskingStrategies.Redaction.StringRedactor;

public class RedactionStrategy extends MaskingStrategy{

    private boolean fullRedaction; // Determines if full or partial redaction is applied

    public RedactionStrategy(boolean fullRedaction) {
        this.fullRedaction = fullRedaction;
    }
    
    public void setFullRedaction(boolean Redaction){
        this.fullRedaction=Redaction;
    }

    public boolean getFullRedaction(){
        return this.fullRedaction;
    }

    public CustomStringList mask(CustomStringList values) {
        if (values == null || values.isEmpty()) {
            return values; // Return as-is if null or empty
        }
    
        StringRedactor stringRedactor = new StringRedactor(); // Use the redactor
    
        CustomStringList maskedList = new CustomStringList();
        values.forEach(value -> maskedList.add(stringRedactor.redactValue(value,this.fullRedaction))); // Apply redaction
    
        return maskedList;
    }

    public CustomIntegerList mask(CustomIntegerList values){
        
        IntegerRedactor integerRedactor= new IntegerRedactor();

        CustomIntegerList maskedList = new CustomIntegerList();
        values.forEach(value -> maskedList.add(integerRedactor.redactValue(value,this.fullRedaction))); // Apply redaction
    
        return maskedList;
    }

    public CustomFloatList mask(CustomFloatList values){
        FloatRedactor floatRedactor= new FloatRedactor();

        CustomFloatList maskedList = new CustomFloatList();
        values.forEach(value -> maskedList.add(floatRedactor.redactValue(value,this.fullRedaction))); // Apply redaction
    
        return maskedList;
    }
}
