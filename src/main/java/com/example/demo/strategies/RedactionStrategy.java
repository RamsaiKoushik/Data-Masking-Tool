package com.example.demo.strategies;

import java.util.List;

import java.util.List;
import java.util.stream.Collectors;

public class RedactionStrategy implements DataMaskingStrategy<String> {

    private final boolean fullRedaction; // Determines if full or partial redaction is applied

    public RedactionStrategy(boolean fullRedaction) {
        this.fullRedaction = fullRedaction;
    }

    public List<String> mask(List<String> values) {
        if (values == null || values.isEmpty()) {
            return values; // Return as-is if null or empty
        }

        return values.stream()
                .map(this::redactValue)
                .collect(Collectors.toList());
    }

    private String redactValue(String value) {
        if (value == null || value.isEmpty()) {
            return value; // Return as-is if null or empty
        }

        if (fullRedaction) {
            return value.replaceAll(".", "*"); // Fully redact all characters
        } else {
            return partiallyRedact(value);
        }
    }

    private String partiallyRedact(String value) {
        int visibleChars = Math.max(1, value.length() / 4); // Keep 25% of the original length visible
        int redactLength = value.length() - visibleChars;

        String visiblePart = value.substring(0, visibleChars);
        String redactedPart = "*".repeat(redactLength);

        return visiblePart + redactedPart;
    }
}

