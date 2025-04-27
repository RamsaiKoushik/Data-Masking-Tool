package datamaskingtool.maskingStrategies.Redaction;

public class FloatRedactor implements Redactor<Float>{

    @Override
    public Float redactValue(Float value, boolean fullRedaction) {
        
        if (fullRedaction) {
            return 0.00f; // Fully redact all characters
        }

        return partiallyRedact(value);
        
    }

    @Override
    public Float partiallyRedact(Float value) {
        if (value == null) return null;
        int intPart = (int) Math.floor(value); 
        return intPart + 0.00f;
    }

    
}
