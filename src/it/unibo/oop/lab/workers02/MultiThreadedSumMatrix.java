package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


/**
 * A Class defining the multi-threaded sum for a matrix.
 */
public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nthread;

    /**
     * 
     * @param nthread
     *            no. of thread performing the sum.
     */
    public MultiThreadedSumMatrix(final int nthread) {
        this.nthread = nthread;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int startpos;
        private final int nelem;
        private double res;

        /**
         * Build a new worker.
         * 
         * @param matrix
         *            the matrix to sum
         * @param startpos
         *            the initial position for this worker
         * @param nelem
         *            the no. of elems to sum up for this worker
         */
        Worker(final double[][] matrix, final int startpos, final int nelem) {
            super();
            this.matrix = matrix;
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        public void run() {
            System.out.println("Working from position " + startpos + " to position " + (startpos + nelem - 1));
            for (int i = startpos; i < matrix.length && i < startpos + nelem; i++) {
                for (final double e : this.matrix[i]) {
                    this.res += e;
                }
            }
        }

        /**
         * Returns the result of summing up the integers within the list.
         * 
         * @return the sum of every element in the array
         */
        public double getResult() {
            return this.res;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double sum(final double[][] matrix) {
        final int size = matrix.length % nthread + matrix.length / nthread;
        /*
         * Build a list of workers
         */
        final List<Worker> workers = new ArrayList<>(nthread);
        for (int start = 0; start < matrix.length; start += size) {
            workers.add(new Worker(matrix, start, size));
        }
        /*
         * Start them
         */
        for (final Worker w: workers) {
            w.start();
        }
        /*
         * Wait for every one of them to finish. This operation is _way_ better done by
         * using barriers and latches, and the whole operation would be better done with
         * futures.
         */
        long sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        /*
         * Return the sum
         */
        return sum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double sumWithStream(final double[][] matrix) {
        final int size = matrix.length % nthread + matrix.length / nthread;
        return IntStream.iterate(0, start -> start + size)
                        .limit(nthread)
                        .mapToObj(start -> new Worker(matrix, start, size))
                        .peek(Thread::start)
                        .peek(t -> {
                            try {
                                t.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        })
                        .mapToDouble(Worker::getResult)
                        .sum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double sumWithParallelStream(final double[][] matrix) {
        final int size = matrix.length % nthread + matrix.length / nthread;
        return IntStream.iterate(0, start -> start + size)
                        .limit(nthread)
                        .parallel()
                        .mapToDouble(start -> {
                            double res = 0;
                            System.out.println("Working from position " + start + " to position " + (start + size - 1));
                            for (int i = start; i < matrix.length && i < start + size; i++) {
                                for (final double e : matrix[i]) {
                                    res += e;
                                }
                            }
                            return res;
                        })
                        .sum();
    }

}
