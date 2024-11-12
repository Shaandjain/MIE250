package Assignments.Assignment03;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class LUDecomposer {
    private Matrix L;
    private Matrix U;
    private Matrix A;
    private boolean isParallel;

    public LUDecomposer(Matrix A, boolean isParallel) {
        this.A = A;
        this.isParallel = isParallel;
    }

    public void decompose() {
        int n = A.getRows();
        L = new Matrix(n, n);
        U = new Matrix(n, n);

        for (int i = 0; i < n; i++) {
            L.set(i, i, 1.0);
        }

        if (isParallel) {
            decomposeParallel();
        } else {
            decomposeSequential();
        }
    }

    private void decomposeSequential() {
        int n = A.getRows();
        for (int i = 0; i < n; i++) {
            // Compute U[i][j]
            for (int j = i; j < n; j++) {
                double sum = 0;
                for (int k = 0; k < i; k++) {
                    sum += L.get(i, k) * U.get(k, j);
                }
                U.set(i, j, A.get(i, j) - sum);
            }

            if (U.get(i, i) == 0) {
                throw new ArithmeticException("Matrix is singular, cannot perform decomposition.");
            }

            // Compute L[j][i]
            for (int j = i + 1; j < n; j++) {
                double sum = 0;
                for (int k = 0; k < i; k++) {
                    sum += L.get(j, k) * U.get(k, i);
                }
                L.set(j, i, (A.get(j, i) - sum) / U.get(i, i));
            }
        }
    }

    private void decomposeParallel() {
        int n = A.getRows();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < n; i++) {
            // Compute U[i][j]
            final int row = i;
            List<Future<?>> futures = new ArrayList<>();

            for (int j = i; j < n; j++) {
                final int col = j;
                Future<?> future = executor.submit(() -> {
                    double sum = 0;
                    for (int k = 0; k < row; k++) {
                        sum += L.get(row, k) * U.get(k, col);
                    }
                    U.set(row, col, A.get(row, col) - sum);
                });
                futures.add(future);
            }

            // Wait for U[i][*] computation to finish
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            if (U.get(i, i) == 0) {
                executor.shutdown();
                throw new ArithmeticException("Matrix is singular, cannot perform decomposition.");
            }

            futures.clear();

            // Create a final copy of 'i' for use inside the lambda
            final int colI = i; // Add this line

            // Compute L[j][i]
            for (int j = i + 1; j < n; j++) {
                final int rowJ = j;
                Future<?> future = executor.submit(() -> {
                    double sum = 0;
                    for (int k = 0; k < colI; k++) { // Use 'colI' instead of 'i'
                        sum += L.get(rowJ, k) * U.get(k, colI);
                    }
                    L.set(rowJ, colI, (A.get(rowJ, colI) - sum) / U.get(colI, colI));
                });
                futures.add(future);
            }
            // Wait for L[*][i] computation to finish
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            futures.clear();
        }
        executor.shutdown();
    }

    public Matrix getL() {
        return L;
    }

    public Matrix getU() {
        return U;
    }
}
