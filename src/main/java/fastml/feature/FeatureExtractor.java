package fastml.feature;

import fastml.pattern.Pattern;

/**
 * Functional interface to extract a numeric feature vector from any input source.
 */
@FunctionalInterface
public interface FeatureExtractor<T> {

    /**
     * Extracts a normalized feature pattern from the given input data.
     *
     * @param input source object (e.g. image, text, stroke sequence)
     * @return extracted feature pattern
     */
    Pattern extract(T input);
}
