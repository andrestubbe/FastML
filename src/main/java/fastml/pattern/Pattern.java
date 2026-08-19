package fastml.pattern;

/**
 * Interface representing any generic feature or raster pattern in FastML.
 */
public interface Pattern {
    /**
     * @return the dimensionality of the pattern representation
     */
    int dimension();

    /**
     * @return numerical representation as a double array
     */
    double[] toArray();
}
