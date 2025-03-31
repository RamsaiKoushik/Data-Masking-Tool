package com.example.demo.strategies;

import java.util.List;
import java.util.Collections;

public class ShufflingStrategy<T> implements DataMaskingStrategy<T> {

    public List<T> mask(List<T> values) {
        if (values == null || values.isEmpty()) {
            return values; // Return as-is if null or empty
        }

        List<T> shuffledList = new java.util.ArrayList<>(List.copyOf(values)); // Create an immutable copy
        Collections.shuffle(shuffledList); // Shuffle the copy

        return shuffledList;
    }
}
