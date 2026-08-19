package fastml.algorithm;

import fastml.Classifier;
import fastml.pattern.Pattern;
import fastml.pattern.VectorPattern;

import java.util.*;

/**
 * Centroid / Nearest-Mean Classifier.
 *
 * <p>Averages multiple training feature vectors per class into a single centroid vector.
 * Predictions calculate Euclidean distance from input to all learned centroids.
 */
public class CentroidClassifier<L> implements Classifier<L> {

    private final Map<L, List<double[]>> samples = new HashMap<>();
    private final Map<L, double[]> centroids = new HashMap<>();
    private boolean dirty = false;

    @Override
    public synchronized void train(L label, Pattern pattern) {
        samples.computeIfAbsent(label, k -> new ArrayList<>()).add(pattern.toArray());
        dirty = true;
    }

    public synchronized void fit() {
        if (!dirty) return;
        centroids.clear();
        for (Map.Entry<L, List<double[]>> entry : samples.entrySet()) {
            centroids.put(entry.getKey(), computeMean(entry.getValue()));
        }
        dirty = false;
    }

    private double[] computeMean(List<double[]> vectors) {
        if (vectors.isEmpty()) return new double[0];
        int dim = vectors.get(0).length;
        double[] mean = new double[dim];
        for (double[] v : vectors) {
            for (int i = 0; i < dim; i++) mean[i] += v[i];
        }
        for (int i = 0; i < dim; i++) mean[i] /= vectors.size();
        return mean;
    }

    @Override
    public synchronized L predict(Pattern pattern) {
        if (dirty) fit();
        if (centroids.isEmpty()) return null;

        double[] input = pattern.toArray();
        L bestLabel = null;
        double minDistance = Double.MAX_VALUE;

        for (Map.Entry<L, double[]> entry : centroids.entrySet()) {
            double dist = euclideanDistance(input, entry.getValue());
            if (dist < minDistance) {
                minDistance = dist;
                bestLabel = entry.getKey();
            }
        }
        return bestLabel;
    }

    @Override
    public synchronized double distance(Pattern pattern, L label) {
        if (dirty) fit();
        double[] c = centroids.get(label);
        if (c == null) return Double.MAX_VALUE;
        return euclideanDistance(pattern.toArray(), c);
    }

    public synchronized Map<L, Double> scoreAll(Pattern pattern) {
        if (dirty) fit();
        Map<L, Double> scores = new HashMap<>();
        double[] input = pattern.toArray();
        for (Map.Entry<L, double[]> entry : centroids.entrySet()) {
            scores.put(entry.getKey(), euclideanDistance(input, entry.getValue()));
        }
        return scores;
    }

    public synchronized VectorPattern getCentroid(L label) {
        if (dirty) fit();
        double[] c = centroids.get(label);
        return c != null ? new VectorPattern(c) : null;
    }

    public synchronized Set<L> getLabels() {
        return Collections.unmodifiableSet(samples.keySet());
    }

    public synchronized void clear() {
        samples.clear();
        centroids.clear();
        dirty = false;
    }

    private static double euclideanDistance(double[] a, double[] b) {
        int len = Math.min(a.length, b.length);
        double sum = 0;
        for (int i = 0; i < len; i++) {
            double d = a[i] - b[i];
            sum += d * d;
        }
        return Math.sqrt(sum);
    }
}
