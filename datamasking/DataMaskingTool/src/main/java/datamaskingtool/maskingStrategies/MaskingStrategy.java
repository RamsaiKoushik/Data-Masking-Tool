package datamaskingtool.maskingStrategies;

import java.util.List;

public abstract class MaskingStrategy {
    public abstract List<String> maskString(List<String> value);
    public abstract List<Integer> maskInteger(List<Integer> value);
    public abstract List<Float> maskFloat(List<Float> value);
}
