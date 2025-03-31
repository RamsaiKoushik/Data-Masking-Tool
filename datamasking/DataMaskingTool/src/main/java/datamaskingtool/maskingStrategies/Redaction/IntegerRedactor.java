package datamaskingtool.maskingStrategies.Redaction;

public class IntegerRedactor implements Redactor<Integer> {
    @Override
    public Integer redactValue(Integer value, boolean fullRedaction) {
        
        if (fullRedaction) {
            return 0; // Fully redact all characters
        }

        return partiallyRedact(value);
        
    }

    @Override
    public Integer partiallyRedact(Integer value) {
        String numStr = value.toString();
        int visibleChars = Math.max(1, numStr.length() / 4); // Keep 25% of the original length visible
        String visiblePart = numStr.substring(0, visibleChars);
        String maskedPart = "*".repeat(numStr.length() - visibleChars);
        
        return Integer.parseInt(visiblePart + maskedPart.replace('*', '0')); // Convert back to Integer
    }
}

