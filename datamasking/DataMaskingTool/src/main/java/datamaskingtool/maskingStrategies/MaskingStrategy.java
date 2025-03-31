package datamaskingtool.maskingStrategies;

import java.util.List;

public abstract class MaskingStrategy {
    public abstract List<String> mask(List<String> value);
    public abstract List<Integer> mask(List<Integer> value);
    public abstract List<Float> mask(List<Float> value);
}
