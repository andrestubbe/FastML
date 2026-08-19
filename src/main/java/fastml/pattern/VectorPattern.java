package fastml.pattern;

import java.util.Arrays;

/**
 * Standard dense numeric feature vector representation.
 */
public record VectorPattern(double[] values) implements Pattern {

    public VectorPattern(double... values) {
        this.values = values != null ? values.clone() : new double[0];
    }

    @Override
    public int dimension() {
        return values.length;
    }

    @Override
    public double[] toArray() {
        return values.clone();
    }

    public double get(int index) {
        return values[index];
    }

    public static VectorPattern of(double... values) {
        return new VectorPattern(values);
    }

    @Override
    public String toString() {
        return "VectorPattern" + Arrays.toString(values);
    }
}
