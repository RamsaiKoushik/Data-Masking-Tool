package datamaskingtool.maskingStrategies.Redaction;

public class BooleanRedactor implements Redactor<Boolean> {

    @Override
    public Boolean redactValue(Boolean value, boolean fullRedaction) {
        if (value == null) {
            return null;
        }

        if (fullRedaction) {
            return false;
        }

        return partiallyRedact(value);
    }

    @Override
    public Boolean partiallyRedact(Boolean value) {
       
        return value;
    }
}
