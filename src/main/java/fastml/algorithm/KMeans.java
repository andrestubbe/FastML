package fastml.algorithm;

import fastml.pattern.VectorPattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * K-Means Clustering Algorithm in FastML.
 */
public class KMeans {

    private final int k;
    private final int maxIterations;
    private final List<VectorPattern> centroids = new ArrayList<>();
    private final List<List<VectorPattern>> clusters = new ArrayList<>();

    public KMeans(int k) {
        this(k, 100);
    }

    public KMeans(int k, int maxIterations) {
        this.k = k;
        this.maxIterations = maxIterations;
    }

    public void fit(List<VectorPattern> points) {
        if (points.isEmpty() || k <= 0) return;

        centroids.clear();
        clusters.clear();
        for (int i = 0; i < k; i++) clusters.add(new ArrayList<>());

        // Initial centroid pick (random subset)
        Random rng = new Random(42);
        for (int i = 0; i < k; i++) {
            centroids.add(points.get(rng.nextInt(points.size())));
        }

        for (int iter = 0; iter < maxIterations; iter++) {
            for (List<VectorPattern> cluster : clusters) cluster.clear();

            // Assign
            for (VectorPattern p : points) {
                int best = 0;
                double minDist = Double.MAX_VALUE;
                for (int i = 0; i < centroids.size(); i++) {
                    double d = distance(p, centroids.get(i));
                    if (d < minDist) {
                        minDist = d;
                        best = i;
                    }
                }
                clusters.get(best).add(p);
            }

            // Update Centroids
            boolean changed = false;
            for (int i = 0; i < k; i++) {
                List<VectorPattern> cluster = clusters.get(i);
                if (cluster.isEmpty()) continue;

                double[] mean = new double[cluster.get(0).dimension()];
                for (VectorPattern p : cluster) {
                    for (int d = 0; d < mean.length; d++) mean[d] += p.get(d);
                }
                for (int d = 0; d < mean.length; d++) mean[d] /= cluster.size();

                VectorPattern newCentroid = new VectorPattern(mean);
                if (distance(newCentroid, centroids.get(i)) > 1e-4) {
                    changed = true;
                    centroids.set(i, newCentroid);
                }
            }
            if (!changed) break;
        }
    }

    public boolean step(List<VectorPattern> points) {
        if (points.isEmpty() || k <= 0) return false;
        if (centroids.isEmpty()) {
            Random rng = new Random();
            for (int i = 0; i < k; i++) {
                centroids.add(points.get(rng.nextInt(points.size())));
                clusters.add(new ArrayList<>());
            }
        }

        for (List<VectorPattern> cluster : clusters) cluster.clear();

        for (VectorPattern p : points) {
            int best = 0;
            double minDist = Double.MAX_VALUE;
            for (int i = 0; i < centroids.size(); i++) {
                double d = distance(p, centroids.get(i));
                if (d < minDist) {
                    minDist = d;
                    best = i;
                }
            }
            clusters.get(best).add(p);
        }

        boolean changed = false;
        for (int i = 0; i < k; i++) {
            List<VectorPattern> cluster = clusters.get(i);
            if (cluster.isEmpty()) continue;

            double[] mean = new double[cluster.get(0).dimension()];
            for (VectorPattern p : cluster) {
                for (int d = 0; d < mean.length; d++) mean[d] += p.get(d);
            }
            for (int d = 0; d < mean.length; d++) mean[d] /= cluster.size();

            VectorPattern newCentroid = new VectorPattern(mean);
            if (distance(newCentroid, centroids.get(i)) > 1e-4) {
                changed = true;
                centroids.set(i, newCentroid);
            }
        }
        return changed;
    }

    public List<VectorPattern> getCentroids() {
        return centroids;
    }

    public List<List<VectorPattern>> getClusters() {
        return clusters;
    }

    private static double distance(VectorPattern a, VectorPattern b) {
        double sum = 0;
        int dim = Math.min(a.dimension(), b.dimension());
        for (int i = 0; i < dim; i++) {
            double d = a.get(i) - b.get(i);
            sum += d * d;
        }
        return Math.sqrt(sum);
    }
}
