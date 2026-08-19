package fastml;

import fastml.algorithm.CentroidClassifier;
import fastml.feature.GeometricFeatureExtractor;
import fastml.pattern.Pattern;
import fastml.pattern.RasterPattern;
import fastml.pattern.VectorPattern;
import fastml.vision.SlidingWindowScanner;

import java.awt.image.BufferedImage;

/**
 * FastML — The classical Machine Learning & pattern recognition substrate of FastJava.
 */
public final class FastML {

    private FastML() {}

    /**
     * Creates a new Centroid / Nearest-Mean classifier.
     *
     * @param <L> Label type (e.g. String, Character, Integer)
     * @return CentroidClassifier instance
     */
    public static <L> CentroidClassifier<L> centroid() {
        return new CentroidClassifier<>();
    }

    /**
     * Creates a standard dense numeric vector pattern.
     */
    public static VectorPattern vector(double... values) {
        return VectorPattern.of(values);
    }

    /**
     * Parses an ASCII grid string into a binary RasterPattern.
     */
    public static RasterPattern raster(String asciiGrid) {
        return RasterPattern.parse(asciiGrid);
    }

    /**
     * Creates a new 2D binary raster pattern of the specified dimensions.
     */
    public static RasterPattern raster(int width, int height) {
        return new RasterPattern(width, height);
    }

    /**
     * Creates a new geometric structural feature extractor for image patches.
     */
    public static GeometricFeatureExtractor geometricFeatures() {
        return new GeometricFeatureExtractor();
    }

    /**
     * Extracts normalized geometric features from a sub-rectangle of an image.
     */
    public static VectorPattern extractFeatures(BufferedImage img, int x, int y, int w, int h) {
        return new GeometricFeatureExtractor().extract(img, x, y, w, h);
    }

    /**
     * Creates a sliding window scanner backed by the given classifier.
     */
    public static <L> SlidingWindowScanner<L> scanner(Classifier<L> classifier) {
        return new SlidingWindowScanner<>(classifier);
    }
}
