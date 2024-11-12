package Assignments.Assignment03;
import java.util.Scanner;
import java.io.*;
import java.util.concurrent.*;

public class LUFactorization {
    
    static class FactorizationResult {
        double[][] L;
        double[][] U;
        double[] tolerance;
    }

    //read matrix from file input
    private static double[][] readMatrix(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        int row = 0;
        int n = -1;
        double[][] matrix = null;
        while ((line = br.readLine()) != null) {
            String[] values = line.trim().split(" ");
            if (n == -1) {
                n = values.length;
                matrix = new double[n][n];
            } else if (values.length != n) {
                br.close();
                throw new IllegalArgumentException("Matrix rows must have the same number of columns.");
            }
            for (int i = 0; i < n; i++) {
                matrix[row][i] = Double.parseDouble(values[i]);
            }
            row++;
            if (row > n) {
                br.close();
                throw new IllegalArgumentException("Matrix must be square (number of rows must equal number of columns).");
            }
        }
        if (row != n) {
            br.close();
            throw new IllegalArgumentException("Matrix must be square (number of rows must equal number of columns).");
        }
        br.close();
        return matrix;
    }

    private static FactorizationResult decompose(double[][] A){
        int n = A.length;
        double[][] L = new double[n][n];
        double[][] U = new double[n][n];
        for (int i = 0; i < n; i++) {
            L[i][i] = 1.0;
        }
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                U[i][j] = A[i][j];
                for (int k = 0; k < i; k++) {
                    U[i][j] -= L[i][k] * U[k][j];
                }
            }
            if (U[i][i] == 0) {
                throw new ArithmeticException("Matrix is singular, cannot perform decomposition.");
            }
            for (int j = i + 1; j < n; j++) {
                L[j][i] = A[j][i];
                for (int k = 0; k < i; k++) {
                    L[j][i] -= L[j][k] * U[k][i];
                }
                L[j][i] /= U[i][i];
            }
        }
        FactorizationResult result = new FactorizationResult();
        result.L = L;
        result.U = U;
        result.tolerance = calculateTolerance(A, L, U);
        return result;
    }

    private static double calculateTolerance(double[][] A, double[][] L, double [][] U){
        int n = A.length;
        double[][] difference = new double[n][n];
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                double sum = 0;
                for (int k = 0; k < n; k++){
                    sum += L[i][k] * U[k][j];
                }
                difference[i][j] = A[i][j] - sum;
            }
        }
        double frobeniusNorm = 0;
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                frobeniusNorm += difference[i][j] * difference[i][j];
            }
        }
        frobeniusNorm = Math.sqrt(frobeniusNorm);
    }

}
