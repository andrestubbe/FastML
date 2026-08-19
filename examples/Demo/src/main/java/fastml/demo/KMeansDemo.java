package fastml.demo;

import fastml.algorithm.KMeans;
import fastml.pattern.VectorPattern;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * FastML Demo 1: K-Means 2D Cluster Visualizer.
 */
public class KMeansDemo extends JFrame {

    private final List<VectorPattern> points = new ArrayList<>();
    private final KMeans kmeans = new KMeans(3);
    private final ClusterCanvas canvas = new ClusterCanvas();
    private final Timer autoPlayTimer;
    private boolean isPlaying = false;

    public KMeansDemo() {
        super("FastML — K-Means 2D Cluster Visualizer");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        JButton btnStep = new JButton("Einzellschritt (Step)");
        JButton btnAuto = new JButton("Auto-Play Animation");
        JButton btnRandom = new JButton("Zufallspunkte erzeugen");
        JButton btnClear = new JButton("Alles löschen");

        controls.add(btnStep);
        controls.add(btnAuto);
        controls.add(btnRandom);
        controls.add(btnClear);
        add(controls, BorderLayout.SOUTH);

        btnStep.addActionListener(e -> {
            kmeans.step(points);
            canvas.repaint();
        });

        btnAuto.addActionListener(e -> {
            isPlaying = !isPlaying;
            btnAuto.setText(isPlaying ? "Pause" : "Auto-Play Animation");
            if (isPlaying) autoPlayTimer.start();
            else autoPlayTimer.stop();
        });

        btnRandom.addActionListener(e -> {
            generateRandomClusters();
            canvas.repaint();
        });

        btnClear.addActionListener(e -> {
            points.clear();
            kmeans.getCentroids().clear();
            kmeans.getClusters().clear();
            canvas.repaint();
        });

        autoPlayTimer = new Timer(300, e -> {
            boolean changed = kmeans.step(points);
            canvas.repaint();
            if (!changed && isPlaying) {
                isPlaying = false;
                btnAuto.setText("Auto-Play Animation");
                autoPlayTimer.stop();
            }
        });

        generateRandomClusters();
    }

    private void generateRandomClusters() {
        points.clear();
        kmeans.getCentroids().clear();
        kmeans.getClusters().clear();

        addGaussianCluster(250, 200, 45, 30);
        addGaussianCluster(650, 250, 50, 30);
        addGaussianCluster(450, 450, 60, 30);
    }

    private void addGaussianCluster(double cx, double cy, double std, int count) {
        java.util.Random r = new java.util.Random();
        for (int i = 0; i < count; i++) {
            double x = cx + r.nextGaussian() * std;
            double y = cy + r.nextGaussian() * std;
            points.add(new VectorPattern(x, y));
        }
    }

    class ClusterCanvas extends JPanel {
        private final Color[] clusterColors = {
                new Color(230, 80, 80),
                new Color(80, 160, 240),
                new Color(80, 200, 120),
                new Color(240, 180, 60),
                new Color(180, 100, 240)
        };

        ClusterCanvas() {
            setBackground(Color.WHITE);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    points.add(new VectorPattern(e.getX(), e.getY()));
                    kmeans.step(points);
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            var clusters = kmeans.getClusters();
            var centroids = kmeans.getCentroids();

            // Draw clustered points
            for (int c = 0; c < clusters.size(); c++) {
                Color col = clusterColors[c % clusterColors.length];
                g2.setColor(col);
                for (VectorPattern p : clusters.get(c)) {
                    g2.fillOval((int) p.get(0) - 4, (int) p.get(1) - 4, 8, 8);
                }
            }

            // Draw unassigned points
            if (clusters.isEmpty()) {
                g2.setColor(Color.GRAY);
                for (VectorPattern p : points) {
                    g2.fillOval((int) p.get(0) - 4, (int) p.get(1) - 4, 8, 8);
                }
            }

            // Draw Centroids
            for (int c = 0; c < centroids.size(); c++) {
                VectorPattern cent = centroids.get(c);
                int cx = (int) cent.get(0);
                int cy = (int) cent.get(1);

                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(cx - 10, cy - 10, 20, 20);

                g2.setColor(clusterColors[c % clusterColors.length]);
                g2.fillOval(cx - 8, cy - 8, 16, 16);

                g2.setColor(Color.BLACK);
                g2.drawString("μ" + (c + 1), cx + 12, cy + 5);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KMeansDemo().setVisible(true));
    }
}
