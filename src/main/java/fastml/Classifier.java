package fastml;

import fastml.pattern.Pattern;

/**
 * Common interface for classification models in FastML.
 */
public interface Classifier<L> {

    /**
     * Trains the model with a single sample.
     *
     * @param label   class/target label
     * @param pattern input feature/raster pattern
     */
    void train(L label, Pattern pattern);

    /**
     * Predicts the label for the given pattern.
     *
     * @param pattern query pattern
     * @return predicted label, or {@code null} if untrained
     */
    L predict(Pattern pattern);

    /**
     * Calculates the distance/dissimilarity metric to a target label.
     *
     * @param pattern query pattern
     * @param label   target label
     * @return distance score (lower is closer/more confident)
     */
    double distance(Pattern pattern, L label);
}
