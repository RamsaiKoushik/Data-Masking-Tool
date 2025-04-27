package datamaskingtool.maskingStrategies.Redaction;

public class StringRedactor implements Redactor<String> {
    @Override
    public String redactValue(String value, boolean fullRedaction) {
        if (value == null || value.isEmpty()) {
            return value; // Return as-is if null or empty
        }

        if (fullRedaction) {
            return value.replaceAll(".", "*"); // Fully redact all characters
        } 
            
        return partiallyRedact(value);
        
    }

    @Override
    public String partiallyRedact(String value) {
        int visibleChars = Math.max(1, value.length() / 2); // Keep 25% of the original length visible
        int redactLength = value.length() - visibleChars;

        String visiblePart = value.substring(0, visibleChars);
        String redactedPart = new String(new char[redactLength]).replace("\0", "*");

        return visiblePart + redactedPart;
    }
}

