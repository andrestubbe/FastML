package fastml.demo;

import fastml.FastML;
import fastml.algorithm.CentroidClassifier;
import fastml.pattern.VectorPattern;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * FastML Demo 3: Mouse Gesture Classifier (DTW & Feature-Based).
 */
public class GestureDemo extends JFrame {

    private final CentroidClassifier<String> gestureModel = FastML.centroid();
    private final List<Point> stroke = new ArrayList<>();
    private final GestureCanvas canvas = new GestureCanvas();
    private final JLabel resultLabel = new JLabel("Mache eine Mausgeste (Kreis, Zickzack, Horizontale Linie, Vertikale Linie)...", JLabel.CENTER);

    public GestureDemo() {
        super("FastML — Real-Time Mouse Gesture Classifier");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        JButton btnTrainCircle = new JButton("Als 'Kreis' trainieren");
        JButton btnTrainZigZag = new JButton("Als 'Zickzack' trainieren");
        JButton btnTrainLineH = new JButton("Als 'Linie-H' trainieren");
        JButton btnTrainLineV = new JButton("Als 'Linie-V' trainieren");
        JButton btnClear = new JButton("Löschen");

        topBar.add(btnTrainCircle);
        topBar.add(btnTrainZigZag);
        topBar.add(btnTrainLineH);
        topBar.add(btnTrainLineV);
        topBar.add(btnClear);
        add(topBar, BorderLayout.NORTH);

        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        resultLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
        add(resultLabel, BorderLayout.SOUTH);

        btnTrainCircle.addActionListener(e -> trainGesture("Kreis"));
        btnTrainZigZag.addActionListener(e -> trainGesture("Zickzack"));
        btnTrainLineH.addActionListener(e -> trainGesture("Linie-H"));
        btnTrainLineV.addActionListener(e -> trainGesture("Linie-V"));
        btnClear.addActionListener(e -> {
            stroke.clear();
            canvas.repaint();
            resultLabel.setText("Canvas gelöscht.");
        });
    }

    private void trainGesture(String label) {
        if (stroke.size() < 5) {
            resultLabel.setText("Fehler: Zu wenige Punkte für eine Geste gezeichnet.");
            return;
        }
        VectorPattern feat = extractGestureFeatures(stroke);
        gestureModel.train(label, feat);
        gestureModel.fit();

        stroke.clear();
        canvas.repaint();
        resultLabel.setText("Geste trainiert als: '" + label + "' (Feature-Vektor: " + feat + ")");
    }

    private void predictGesture() {
        if (stroke.size() < 5) return;
        if (gestureModel.getLabels().isEmpty()) {
            resultLabel.setText("Modell enthält noch keine trainierten Gesten.");
            return;
        }

        VectorPattern feat = extractGestureFeatures(stroke);
        String pred = gestureModel.predict(feat);
        double dist = gestureModel.distance(feat, pred);

        resultLabel.setText(String.format("Erkannte Geste: '%s' (Distanz: %.3f)", pred, dist));
    }

    private static VectorPattern extractGestureFeatures(List<Point> pts) {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
        double totalLen = 0;

        for (int i = 0; i < pts.size(); i++) {
            Point p = pts.get(i);
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);

            if (i > 0) {
                Point prev = pts.get(i - 1);
                totalLen += Math.hypot(p.x - prev.x, p.y - prev.y);
            }
        }

        double w = Math.max(1.0, maxX - minX);
        double h = Math.max(1.0, maxY - minY);
        double aspect = w / h;

        Point start = pts.get(0);
        Point end = pts.get(pts.size() - 1);
        double endDistance = Math.hypot(end.x - start.x, end.y - start.y);
        double closedness = endDistance / Math.max(1.0, totalLen); // near 0 for closed circle

        // Direction changes / inflections
        int directionChanges = 0;
        for (int i = 2; i < pts.size(); i++) {
            double dx1 = pts.get(i - 1).x - pts.get(i - 2).x;
            double dx2 = pts.get(i).x - pts.get(i - 1).x;
            if (dx1 * dx2 < 0) directionChanges++;
        }

        return new VectorPattern(aspect, closedness, directionChanges, totalLen / Math.max(w, h));
    }

    class GestureCanvas extends JPanel {
        GestureCanvas() {
            setBackground(Color.WHITE);
            MouseAdapter m = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    stroke.clear();
                    stroke.add(e.getPoint());
                    repaint();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    stroke.add(e.getPoint());
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    predictGesture();
                    repaint();
                }
            };
            addMouseListener(m);
            addMouseMotionListener(m);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (stroke.size() > 1) {
                g2.setColor(new Color(50, 120, 240));
                g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                for (int i = 1; i < stroke.size(); i++) {
                    Point p1 = stroke.get(i - 1);
                    Point p2 = stroke.get(i);
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GestureDemo().setVisible(true));
    }
}
