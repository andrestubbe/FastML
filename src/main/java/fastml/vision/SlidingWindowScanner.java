package fastml.vision;

import fastml.Classifier;
import fastml.feature.GeometricFeatureExtractor;
import fastml.pattern.VectorPattern;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Sliding window detection engine that scans target images for pattern matches using a trained classifier.
 */
public class SlidingWindowScanner<L> {

    public record Match<L>(int x, int y, int width, int height, L label, double distance) {}

    private final Classifier<L> classifier;
    private final GeometricFeatureExtractor extractor;

    public SlidingWindowScanner(Classifier<L> classifier) {
        this(classifier, new GeometricFeatureExtractor());
    }

    public SlidingWindowScanner(Classifier<L> classifier, GeometricFeatureExtractor extractor) {
        this.classifier = classifier;
        this.extractor = extractor;
    }

    /**
     * Scans an image with a sliding window of size (winW, winH) and step stride (stepX, stepY).
     *
     * @param img            source image
     * @param winW           window width
     * @param winH           window height
     * @param stepX          horizontal stride
     * @param stepY          vertical stride
     * @param targetLabel    specific label to match, or {@code null} to match best prediction
     * @param maxDistance    maximum distance threshold to count as a match
     * @return list of matching windows
     */
    public List<Match<L>> scan(BufferedImage img, int winW, int winH, int stepX, int stepY, L targetLabel, double maxDistance) {
        List<Match<L>> matches = new ArrayList<>();
        if (img == null) return matches;

        int imgW = img.getWidth();
        int imgH = img.getHeight();

        for (int y = 0; y <= imgH - winH; y += stepY) {
            for (int x = 0; x <= imgW - winW; x += stepX) {
                VectorPattern feat = extractor.extract(img, x, y, winW, winH);

                if (targetLabel != null) {
                    double dist = classifier.distance(feat, targetLabel);
                    if (dist <= maxDistance) {
                        matches.add(new Match<>(x, y, winW, winH, targetLabel, dist));
                    }
                } else {
                    L pred = classifier.predict(feat);
                    if (pred != null) {
                        double dist = classifier.distance(feat, pred);
                        if (dist <= maxDistance) {
                            matches.add(new Match<>(x, y, winW, winH, pred, dist));
                        }
                    }
                }
            }
        }
        return matches;
    }
}
