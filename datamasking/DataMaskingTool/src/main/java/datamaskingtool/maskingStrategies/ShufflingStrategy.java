package datamaskingtool.maskingStrategies;

import java.util.Collections;
import java.util.List;

public class ShufflingStrategy extends MaskingStrategy{
    public List<String> mask(List<String> values){

        if (values == null || values.isEmpty()) {
            return values; // Return as-is if null or empty
        }

        List<String> shuffledList = new java.util.ArrayList<>(List.copyOf(values)); // Create an immutable copy
        Collections.shuffle(shuffledList); // Shuffle the copy

        return shuffledList;
    }

    public List<Integer> mask(List<Integer> values){
        if (values == null || values.isEmpty()) {
            return values; // Return as-is if null or empty
        }

        List<Integer> shuffledList = new java.util.ArrayList<>(List.copyOf(values)); // Create an immutable copy
        Collections.shuffle(shuffledList); // Shuffle the copy

        return shuffledList;
    }

    public List<Float> mask(List<Float> values){
        if (values == null || values.isEmpty()) {
            return values; // Return as-is if null or empty
        }

        List<Integer> shuffledList = new java.util.ArrayList<>(List.copyOf(values)); // Create an immutable copy
        Collections.shuffle(shuffledList); // Shuffle the copy

        return shuffledList;
    }
}
