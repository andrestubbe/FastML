package fastml.algorithm;

import fastml.Classifier;
import fastml.pattern.Pattern;
import fastml.pattern.VectorPattern;

import java.util.*;

/**
 * K-Nearest Neighbors Classifier in FastML.
 */
public class KNNClassifier<L> implements Classifier<L> {

    public record Entry<L>(VectorPattern pattern, L label) {}

    private final int k;
    private final List<Entry<L>> samples = new ArrayList<>();

    public KNNClassifier(int k) {
        this.k = Math.max(1, k);
    }

    @Override
    public synchronized void train(L label, Pattern pattern) {
        samples.add(new Entry<>(new VectorPattern(pattern.toArray()), label));
    }

    @Override
    public synchronized L predict(Pattern pattern) {
        if (samples.isEmpty()) return null;

        VectorPattern query = new VectorPattern(pattern.toArray());
        PriorityQueue<Map.Entry<Double, L>> pq = new PriorityQueue<>(
                Comparator.comparingDouble((Map.Entry<Double, L> e) -> e.getKey()).reversed()
        );

        for (Entry<L> s : samples) {
            double d = distance(query, s.pattern);
            pq.offer(new AbstractMap.SimpleEntry<>(d, s.label));
            if (pq.size() > k) {
                pq.poll();
            }
        }

        Map<L, Integer> votes = new HashMap<>();
        while (!pq.isEmpty()) {
            L lbl = pq.poll().getValue();
            votes.put(lbl, votes.getOrDefault(lbl, 0) + 1);
        }

        return votes.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    @Override
    public synchronized double distance(Pattern pattern, L label) {
        VectorPattern query = new VectorPattern(pattern.toArray());
        return samples.stream()
                .filter(s -> Objects.equals(s.label, label))
                .mapToDouble(s -> distance(query, s.pattern))
                .min()
                .orElse(Double.MAX_VALUE);
    }

    public synchronized List<Entry<L>> getSamples() {
        return Collections.unmodifiableList(samples);
    }

    public synchronized void clear() {
        samples.clear();
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
