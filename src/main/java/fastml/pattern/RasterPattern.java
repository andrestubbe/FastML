package fastml.pattern;

import java.util.Arrays;

/**
 * 2D binary grid pattern (e.g. 8x8, 16x16, 32x32 character rasters).
 */
public final class RasterPattern implements Pattern {

    private final int width;
    private final int height;
    private final byte[] grid;

    public RasterPattern(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new byte[width * height];
    }

    public RasterPattern(int width, int height, byte[] data) {
        this.width = width;
        this.height = height;
        this.grid = data != null ? data.clone() : new byte[width * height];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void set(int x, int y, boolean on) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            grid[y * width + x] = (byte) (on ? 1 : 0);
        }
    }

    public boolean get(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return grid[y * width + x] == 1;
        }
        return false;
    }

    @Override
    public int dimension() {
        return grid.length;
    }

    @Override
    public double[] toArray() {
        double[] out = new double[grid.length];
        for (int i = 0; i < grid.length; i++) {
            out[i] = grid[i];
        }
        return out;
    }

    public static RasterPattern parse(String asciiGrid) {
        String[] lines = Arrays.stream(asciiGrid.strip().split("\\r?\\n"))
                .map(String::trim)
                .filter(l -> !l.isEmpty())
                .toArray(String[]::new);

        int h = lines.length;
        if (h == 0) return new RasterPattern(0, 0);

        String[] firstTokens = lines[0].split("\\s+");
        int w = firstTokens.length;

        RasterPattern pattern = new RasterPattern(w, h);
        for (int y = 0; y < h; y++) {
            String[] tokens = lines[y].split("\\s+");
            for (int x = 0; x < Math.min(w, tokens.length); x++) {
                pattern.set(x, y, "1".equals(tokens[x]) || "#".equals(tokens[x]) || "X".equalsIgnoreCase(tokens[x]));
            }
        }
        return pattern;
    }
}
