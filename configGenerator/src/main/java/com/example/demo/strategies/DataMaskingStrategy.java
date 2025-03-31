package com.example.demo.strategies;
import java.util.*;

public interface DataMaskingStrategy<T> {
    List<T> mask(List<T> values);
}
