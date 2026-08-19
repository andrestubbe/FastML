package fastml.demo;

import fastml.FastML;
import fastml.algorithm.CentroidClassifier;
import fastml.pattern.RasterPattern;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * FastML Handwriting Canvas & Memory Visualizer Demo (inspired by Demo_04).
 *
 * <p>Features:
 * <ul>
 *   <li>Left panel: Interactive freehand drawing canvas with resolution-independent strokes</li>
 *   <li>Normalized 28x28 rasterization</li>
 *   <li>Right panel: Real-time visual Heatmap memory / Centroid of learned letters</li>
 *   <li>Immediate prediction and distance measurement</li>
 * </ul>
 */
public class HandwritingDemo extends JFrame {

    private static final int TARGET_GRID = 28;

    private final CentroidClassifier<String> model = FastML.centroid();
    private final DrawCanvas canvas = new DrawCanvas();
    private final CentroidMemoryPanel memoryPanel = new CentroidMemoryPanel();
    private final JLabel statusLabel = new JLabel("Status: Zeichne einen Buchstaben links...", JLabel.CENTER);

    public HandwritingDemo() {
        super("FastML — Handwriting & Centroid Memory Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 620);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        // Top Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton btnTrainA = new JButton("Als 'A' trainieren");
        JButton btnTrainB = new JButton("Als 'B' trainieren");
        JButton btnTrainC = new JButton("Als 'C' trainieren");
        JButton btnTrainCustom = new JButton("Eigenes Label trainieren...");
        JButton btnPredict = new JButton("Erkennen (Predict)");
        JButton btnClear = new JButton("Canvas Löschen");

        toolbar.add(btnTrainA);
        toolbar.add(btnTrainB);
        toolbar.add(btnTrainC);
        toolbar.add(btnTrainCustom);
        toolbar.add(Box.createHorizontalStrut(15));
        toolbar.add(btnPredict);
        toolbar.add(btnClear);

        add(toolbar, BorderLayout.NORTH);

        // Center Split / Dual Display
        JPanel mainDisplay = new JPanel(new GridLayout(1, 2, 15, 10));
        mainDisplay.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel leftBox = new JPanel(new BorderLayout(5, 5));
        leftBox.setBorder(BorderFactory.createTitledBorder("Eingabe-Canvas (Zeichnen)"));
        leftBox.add(new SquareWrapper(canvas), BorderLayout.CENTER);

        JPanel rightBox = new JPanel(new BorderLayout(5, 5));
        rightBox.setBorder(BorderFactory.createTitledBorder("Visuelles Gedächtnis (28x28 Zentroid)"));
        rightBox.add(new SquareWrapper(memoryPanel), BorderLayout.CENTER);

        mainDisplay.add(leftBox);
        mainDisplay.add(rightBox);
        add(mainDisplay, BorderLayout.CENTER);

        // Bottom Status Bar
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 8, 10));
        add(statusLabel, BorderLayout.SOUTH);

        // Button Actions
        btnTrainA.addActionListener(e -> trainCurrentLabel("A"));
        btnTrainB.addActionListener(e -> trainCurrentLabel("B"));
        btnTrainC.addActionListener(e -> trainCurrentLabel("C"));
        btnTrainCustom.addActionListener(e -> {
            String label = JOptionPane.showInputDialog(this, "Buchstabe / Label:", "A");
            if (label != null && !label.trim().isEmpty()) {
                trainCurrentLabel(label.trim().toUpperCase());
            }
        });

        btnPredict.addActionListener(e -> predictCurrent());
        btnClear.addActionListener(e -> {
            canvas.clear();
            statusLabel.setText("Canvas geleert.");
        });
    }

    private void trainCurrentLabel(String label) {
        RasterPattern pattern = canvas.toRasterPattern(TARGET_GRID);
        if (pattern == null) {
            statusLabel.setText("Fehler: Canvas ist leer. Bitte zuerst zeichnen.");
            return;
        }

        model.train(label, pattern);
        model.fit();

        var centroid = model.getCentroid(label);
        if (centroid != null) {
            memoryPanel.setCentroid(label, centroid.toArray(), TARGET_GRID);
        }

        canvas.clear();
        statusLabel.setText("Erfolgreich trainiert: Label '" + label + "'. Memory aktualisiert.");
    }

    private void predictCurrent() {
        RasterPattern pattern = canvas.toRasterPattern(TARGET_GRID);
        if (pattern == null) {
            statusLabel.setText("Fehler: Kein Zeichen zum Erkennen vorhanden.");
            return;
        }

        String pred = model.predict(pattern);
        if (pred == null) {
            statusLabel.setText("Keine Vorhersage möglich: Modell ist noch untrainiert.");
            return;
        }

        double dist = model.distance(pattern, pred);
        var centroid = model.getCentroid(pred);
        if (centroid != null) {
            memoryPanel.setCentroid(pred, centroid.toArray(), TARGET_GRID);
        }

        statusLabel.setText(String.format("Erkannt: '%s' (Distanz: %.3f)", pred, dist));
    }

    // -------------------------------------------------------------------------
    //  Square Wrapper for equal aspect ratio
    // -------------------------------------------------------------------------
    static class SquareWrapper extends JPanel {
        private final JComponent inner;

        SquareWrapper(JComponent inner) {
            this.inner = inner;
            setLayout(null);
            add(inner);
        }

        @Override
        public void doLayout() {
            int size = Math.min(getWidth(), getHeight()) - 10;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            inner.setBounds(x, y, size, size);
        }
    }

    // -------------------------------------------------------------------------
    //  Draw Canvas
    // -------------------------------------------------------------------------
    static class DrawCanvas extends JPanel {
        private final List<List<Point>> strokes = new ArrayList<>();
        private List<Point> currentStroke;

        DrawCanvas() {
            setBackground(Color.WHITE);
            MouseAdapter mouse = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    currentStroke = new ArrayList<>();
                    currentStroke.add(e.getPoint());
                    strokes.add(currentStroke);
                    repaint();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (currentStroke != null) {
                        currentStroke.add(e.getPoint());
                        repaint();
                    }
                }
            };
            addMouseListener(mouse);
            addMouseMotionListener(mouse);
        }

        void clear() {
            strokes.clear();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            for (List<Point> stroke : strokes) {
                for (int i = 1; i < stroke.size(); i++) {
                    Point p1 = stroke.get(i - 1);
                    Point p2 = stroke.get(i);
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }

            g2.setColor(new Color(220, 220, 220));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }

        public RasterPattern toRasterPattern(int grid) {
            if (strokes.isEmpty()) return null;

            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

            for (List<Point> stroke : strokes) {
                for (Point p : stroke) {
                    minX = Math.min(minX, p.x);
                    minY = Math.min(minY, p.y);
                    maxX = Math.max(maxX, p.x);
                    maxY = Math.max(maxY, p.y);
                }
            }

            double width = Math.max(1.0, maxX - minX);
            double height = Math.max(1.0, maxY - minY);

            RasterPattern pattern = FastML.raster(grid, grid);

            for (List<Point> stroke : strokes) {
                for (Point p : stroke) {
                    double nx = (p.x - minX) / width;
                    double ny = (p.y - minY) / height;

                    int gx = Math.min(grid - 1, Math.max(0, (int) (nx * (grid - 1))));
                    int gy = Math.min(grid - 1, Math.max(0, (int) (ny * (grid - 1))));

                    pattern.set(gx, gy, true);
                }
            }
            return pattern;
        }
    }

    // -------------------------------------------------------------------------
    //  Centroid Memory Visualizer Panel (Heatmap)
    // -------------------------------------------------------------------------
    static class CentroidMemoryPanel extends JPanel {
        private double[] centroid;
        private int gridSize = TARGET_GRID;
        private String currentLabel = "";

        CentroidMemoryPanel() {
            setBackground(new Color(245, 245, 245));
        }

        void setCentroid(String label, double[] centroid, int gridSize) {
            this.currentLabel = label;
            this.centroid = centroid;
            this.gridSize = gridSize;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth();
            int h = getHeight();

            if (centroid == null) {
                g.setColor(Color.GRAY);
                g.drawString("Noch kein Modell geladen/trainiert", w / 2 - 100, h / 2);
                return;
            }

            double cellW = (double) w / gridSize;
            double cellH = (double) h / gridSize;

            for (int y = 0; y < gridSize; y++) {
                for (int x = 0; x < gridSize; x++) {
                    double v = centroid[y * gridSize + x];
                    v = Math.min(1.0, Math.max(0.0, v));

                    // Invert: 0.0 -> white (255), 1.0 -> black/blue density (0)
                    int gray = (int) (255 * (1.0 - v));
                    g.setColor(new Color(gray, gray, (int) (gray * 0.9 + 25 * (1.0 - v))));
                    g.fillRect((int) (x * cellW), (int) (y * cellH), (int) Math.ceil(cellW), (int) Math.ceil(cellH));
                }
            }

            // Grid lines
            g.setColor(new Color(220, 220, 220, 100));
            for (int i = 0; i <= gridSize; i++) {
                g.drawLine((int) (i * cellW), 0, (int) (i * cellW), h);
                g.drawLine(0, (int) (i * cellH), w, (int) (i * cellH));
            }

            // Overlay label
            if (!currentLabel.isEmpty()) {
                g.setColor(new Color(0, 100, 255, 180));
                g.setFont(new Font("SansSerif", Font.BOLD, 22));
                g.drawString("Zentroid: '" + currentLabel + "'", 12, 28);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HandwritingDemo().setVisible(true));
    }
}
