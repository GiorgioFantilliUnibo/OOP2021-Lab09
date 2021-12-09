package it.unibo.oop.lab.workers02;

/**
 * 
 * An interfaced defining a sum for a matrix.
 *
 */
public interface SumMatrix {

    /**
     * Return the sum of {@link matrix} elements.
     * 
     * @param matrix
     *            an arbitrary-sized matrix
     * @return the sum of its elements
     */
    double sum(double[][] matrix);
    
    /**
     * Return the sum of {@link matrix} elements using streams.
     * 
     * @param matrix
     *            an arbitrary-sized matrix
     * @return the sum of its elements
     */
    double sumWithStream(double[][] matrix);
    
    /**
     * Return the sum of {@link matrix} elements using parallel streams.
     * 
     * @param matrix
     *            an arbitrary-sized matrix
     * @return the sum of its elements
     */
    double sumWithParallelStream(double[][] matrix);

}
