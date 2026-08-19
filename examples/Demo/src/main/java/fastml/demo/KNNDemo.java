package fastml.demo;

import fastml.algorithm.KNNClassifier;
import fastml.pattern.VectorPattern;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * FastML Demo 2: K-Nearest-Neighbors (KNN) Decision Boundary Visualizer.
 */
public class KNNDemo extends JFrame {

    private final KNNClassifier<Integer> knn = new KNNClassifier<>(3);
    private int currentClass = 1;
    private int currentK = 3;
    private final BoundaryCanvas canvas = new BoundaryCanvas();

    public KNNDemo() {
        super("FastML — K-Nearest Neighbors Decision Boundary Visualizer");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        JButton btnClass1 = new JButton("Klasse 1 (Rot)");
        JButton btnClass2 = new JButton("Klasse 2 (Blau)");
        JButton btnClass3 = new JButton("Klasse 3 (Grün)");

        JLabel lblK = new JLabel("k Nachbarn: 3");
        JSlider sliderK = new JSlider(1, 15, 3);
        JButton btnClear = new JButton("Löschen");

        controls.add(btnClass1);
        controls.add(btnClass2);
        controls.add(btnClass3);
        controls.add(Box.createHorizontalStrut(10));
        controls.add(lblK);
        controls.add(sliderK);
        controls.add(btnClear);
        add(controls, BorderLayout.SOUTH);

        btnClass1.addActionListener(e -> currentClass = 1);
        btnClass2.addActionListener(e -> currentClass = 2);
        btnClass3.addActionListener(e -> currentClass = 3);

        sliderK.addChangeListener(e -> {
            currentK = sliderK.getValue();
            lblK.setText("k Nachbarn: " + currentK);
            rebuildKNN();
            canvas.repaint();
        });

        btnClear.addActionListener(e -> {
            knn.clear();
            canvas.repaint();
        });

        initInitialSamples();
    }

    private void initInitialSamples() {
        knn.train(1, new VectorPattern(250, 250));
        knn.train(1, new VectorPattern(280, 270));
        knn.train(1, new VectorPattern(240, 310));

        knn.train(2, new VectorPattern(600, 220));
        knn.train(2, new VectorPattern(630, 260));
        knn.train(2, new VectorPattern(580, 300));

        knn.train(3, new VectorPattern(420, 500));
        knn.train(3, new VectorPattern(470, 530));
        knn.train(3, new VectorPattern(440, 470));
    }

    private void rebuildKNN() {
        var old = knn.getSamples();
        KNNClassifier<Integer> newKnn = new KNNClassifier<>(currentK);
        for (var s : old) {
            newKnn.train(s.label(), s.pattern());
        }
        knn.clear();
        for (var s : newKnn.getSamples()) {
            knn.train(s.label(), s.pattern());
        }
    }

    class BoundaryCanvas extends JPanel {
        private final Color[] classPointColors = {
                Color.GRAY,
                new Color(220, 40, 40),
                new Color(40, 100, 240),
                new Color(40, 180, 60)
        };

        private final Color[] classBgColors = {
                Color.WHITE,
                new Color(255, 220, 220),
                new Color(220, 235, 255),
                new Color(220, 255, 225)
        };

        BoundaryCanvas() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    knn.train(currentClass, new VectorPattern(e.getX(), e.getY()));
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth();
            int h = getHeight();
            int step = 8;

            // Render Decision Boundary Raster
            if (!knn.getSamples().isEmpty()) {
                for (int y = 0; y < h; y += step) {
                    for (int x = 0; x < w; x += step) {
                        Integer pred = knn.predict(new VectorPattern(x, y));
                        if (pred != null && pred >= 1 && pred <= 3) {
                            g.setColor(classBgColors[pred]);
                            g.fillRect(x, y, step, step);
                        }
                    }
                }
            }

            // Render Training Sample Points
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (var s : knn.getSamples()) {
                int px = (int) s.pattern().get(0);
                int py = (int) s.pattern().get(1);
                int lbl = s.label();

                g2.setColor(Color.BLACK);
                g2.fillOval(px - 6, py - 6, 12, 12);
                g2.setColor(classPointColors[lbl]);
                g2.fillOval(px - 5, py - 5, 10, 10);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KNNDemo().setVisible(true));
    }
}
