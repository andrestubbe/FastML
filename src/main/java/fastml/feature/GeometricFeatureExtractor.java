package fastml.feature;

import fastml.pattern.VectorPattern;

import java.awt.image.BufferedImage;

/**
 * 8-dimensional normalized structural & geometric feature extractor for raster / handwriting patches.
 *
 * <p>Features extracted (all normalized):
 * <ul>
 *   <li>0: Stroke count approximation</li>
 *   <li>1: Total stroke length / window width</li>
 *   <li>2: Average segment length / window width</li>
 *   <li>3: Normalized bounding box width (w / windowWidth)</li>
 *   <li>4: Normalized bounding box height (h / windowHeight)</li>
 *   <li>5: Aspect ratio (w / h)</li>
 *   <li>6: Normalized horizontal center of mass</li>
 *   <li>7: Normalized vertical center of mass</li>
 * </ul>
 */
public class GeometricFeatureExtractor implements FeatureExtractor<GeometricFeatureExtractor.Window> {

    public static final int FEATURE_DIM = 8;
    private final int threshold;

    public record Window(BufferedImage image, int x, int y, int width, int height) {}

    public GeometricFeatureExtractor() {
        this(128);
    }

    public GeometricFeatureExtractor(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public VectorPattern extract(Window window) {
        return extract(window.image(), window.x(), window.y(), window.width(), window.height());
    }

    public VectorPattern extract(BufferedImage img, int x0, int y0, int w, int h) {
        double[] f = new double[FEATURE_DIM];
        if (img == null || w <= 0 || h <= 0) return new VectorPattern(f);

        int x1 = Math.min(img.getWidth(), x0 + w);
        int y1 = Math.min(img.getHeight(), y0 + h);

        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

        for (int y = y0; y < y1; y++) {
            for (int x = x0; x < x1; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int gray = (r + g + b) / 3;
                if (gray < threshold) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        if (minX == Double.MAX_VALUE) {
            return new VectorPattern(f);
        }

        double bboxW = Math.max(1.0, maxX - minX);
        double bboxH = Math.max(1.0, maxY - minY);
        double aspect = bboxW / bboxH;

        double centerX = (minX + maxX) / 2.0;
        double centerY = (minY + maxY) / 2.0;

        double normCenterX = (centerX - x0) / w;
        double normCenterY = (centerY - y0) / h;
        double normW = bboxW / w;
        double normH = bboxH / h;

        int strokeCountApprox = 0;
        double totalLengthApprox = 0.0;
        double totalSegmentsApprox = 0.0;

        for (int y = (int) minY; y <= (int) maxY; y++) {
            boolean inStroke = false;
            int lastX = -1;
            for (int x = (int) minX; x <= (int) maxX; x++) {
                int rgb = img.getRGB(x, y);
                int gray = (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;

                if (gray < threshold) {
                    if (!inStroke) {
                        inStroke = true;
                        strokeCountApprox++;
                    }
                    if (lastX != -1) {
                        double dx = x - lastX;
                        totalLengthApprox += Math.abs(dx);
                        totalSegmentsApprox += 1.0;
                    }
                    lastX = x;
                } else {
                    inStroke = false;
                    lastX = -1;
                }
            }
        }

        double avgSegmentLength = totalSegmentsApprox > 0 ? totalLengthApprox / totalSegmentsApprox : 0.0;

        f[0] = strokeCountApprox;
        f[1] = totalLengthApprox / w;
        f[2] = avgSegmentLength / w;
        f[3] = normW;
        f[4] = normH;
        f[5] = aspect;
        f[6] = normCenterX;
        f[7] = normCenterY;

        return new VectorPattern(f);
    }
}
