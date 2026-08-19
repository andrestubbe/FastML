package fastml.demo;

import fastml.FastML;
import fastml.algorithm.CentroidClassifier;
import fastml.vision.SlidingWindowScanner;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * FastML Handwriting Recognition & Sliding Window Detection Demo.
 */
public class HandwritingDemo extends JFrame {

    private final CentroidClassifier<String> model = FastML.centroid();
    private final SlidingWindowScanner<String> scanner = FastML.scanner(model);

    private final ImagePanel sourcePanel = new ImagePanel("Trainings-Vorschau");
    private final ImagePanel targetPanel = new ImagePanel("Zielbild (Erkennung)");
    private final JTextArea logArea = new JTextArea(8, 40);

    public HandwritingDemo() {
        super("FastML — Handwriting & Pattern Recognition Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));

        JPanel imagesPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        imagesPanel.add(sourcePanel);
        imagesPanel.add(targetPanel);
        add(imagesPanel, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton btnLoadTrain = new JButton("1. Trainingsbild laden");
        JButton btnTrain = new JButton("2. Label trainieren");
        JButton btnLoadTarget = new JButton("3. Zielbild laden");
        JButton btnScan = new JButton("4. Muster scannen");

        controls.add(btnLoadTrain);
        controls.add(btnTrain);
        controls.add(btnLoadTarget);
        controls.add(btnScan);
        add(controls, BorderLayout.NORTH);

        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(new JScrollPane(logArea), BorderLayout.SOUTH);

        btnLoadTrain.addActionListener(e -> chooseImage(sourcePanel, "Trainingsbild geladen"));
        btnTrain.addActionListener(e -> trainCurrentSource());
        btnLoadTarget.addActionListener(e -> chooseImage(targetPanel, "Zielbild geladen"));
        btnScan.addActionListener(e -> scanTarget());

        log("FastML Initialisiert. Bereit für Training und Inferenz.");
    }

    private void chooseImage(ImagePanel panel, String msg) {
        JFileChooser chooser = new JFileChooser(".");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                BufferedImage img = ImageIO.read(file);
                panel.setImage(img);
                log(msg + ": " + file.getName() + " (" + img.getWidth() + "x" + img.getHeight() + ")");
            } catch (Exception ex) {
                log("Fehler beim Laden: " + ex.getMessage());
            }
        }
    }

    private void trainCurrentSource() {
        if (sourcePanel.image == null) {
            log("Bitte zuerst ein Trainingsbild laden.");
            return;
        }
        String label = JOptionPane.showInputDialog(this, "Label für dieses Muster eingeben:", "A");
        if (label == null || label.trim().isEmpty()) return;

        label = label.trim();
        var feat = FastML.extractFeatures(sourcePanel.image, 0, 0, sourcePanel.image.getWidth(), sourcePanel.image.getHeight());
        model.train(label, feat);
        model.fit();

        log("Label '" + label + "' trainiert mit Feature-Vektor: " + feat);
    }

    private void scanTarget() {
        if (targetPanel.image == null) {
            log("Bitte zuerst ein Zielbild laden.");
            return;
        }
        if (model.getLabels().isEmpty()) {
            log("Modell enthält noch keine trainierten Klassen.");
            return;
        }

        int winW = 60;
        int winH = 60;
        int step = 15;
        double maxDist = 2.5;

        log("Scanne Zielbild (Fenster: " + winW + "x" + winH + ", Stride: " + step + ", MaxDist: " + maxDist + ")...");
        var matches = scanner.scan(targetPanel.image, winW, winH, step, step, null, maxDist);

        targetPanel.setMatches(matches);
        log("Scan abgeschlossen: " + matches.size() + " Treffer gefunden.");
        for (var m : matches) {
            log("  -> Label: " + m.label() + " @ (" + m.x() + ", " + m.y() + ") Dist: " + String.format("%.3f", m.distance()));
        }
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    static class ImagePanel extends JPanel {
        private BufferedImage image;
        private final String title;
        private List<SlidingWindowScanner.Match<String>> matches = new ArrayList<>();

        ImagePanel(String title) {
            this.title = title;
            setBorder(BorderFactory.createTitledBorder(title));
        }

        void setImage(BufferedImage img) {
            this.image = img;
            this.matches.clear();
            repaint();
        }

        void setMatches(List<SlidingWindowScanner.Match<String>> matches) {
            this.matches = matches;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image == null) {
                g.setColor(Color.GRAY);
                g.drawString("Kein Bild geladen", getWidth() / 2 - 50, getHeight() / 2);
                return;
            }

            double sx = (double) getWidth() / image.getWidth();
            double sy = (double) getHeight() / image.getHeight();

            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

            g.setColor(new Color(255, 0, 0, 180));
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));

            for (var m : matches) {
                int rx = (int) (m.x() * sx);
                int ry = (int) (m.y() * sy);
                int rw = (int) (m.width() * sx);
                int rh = (int) (m.height() * sy);
                g.drawRect(rx, ry, rw, rh);
                g.drawString(m.label() + " (" + String.format("%.2f", m.distance()) + ")", rx + 4, ry + 15);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HandwritingDemo().setVisible(true));
    }
}
