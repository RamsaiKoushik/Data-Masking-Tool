package datamaskingtool.maskingStrategies.Redaction;

public interface Redactor<T> {
    T redactValue(T value, boolean fullRedaction);
    T partiallyRedact(T value);
}
