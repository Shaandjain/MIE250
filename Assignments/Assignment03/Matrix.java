package Assignments.Assignment03;

import java.io.BufferedWriter;
import java.io.IOException;

public class Matrix {
    private int rows;
    private int cols;
    private double[][] data;

    // Constructor
    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        data = new double[rows][cols];
    }
    public void write(BufferedWriter writer) throws IOException {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                writer.write(String.format("%.4f ", data[i][j]));
            }
            writer.newLine();
        }
    }

    // Constructor with data
    public Matrix(double[][] data) {
        this.rows = data.length;
        this.cols = data[0].length;
        this.data = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(data[i], 0, this.data[i], 0, cols);
        }
    }

    // Getters
    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    // Get element
    public double get(int i, int j) {
        return data[i][j];
    }

    // Set element
    public void set(int i, int j, double value) {
        data[i][j] = value;
    }

    // Matrix multiplication
    public Matrix multiply(Matrix other) {
        if (this.cols != other.rows) {
            throw new IllegalArgumentException("Incompatible matrix sizes for multiplication.");
        }
        Matrix result = new Matrix(this.rows, other.cols);
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < other.cols; j++) {
                double sum = 0;
                for (int k = 0; k < this.cols; k++) {
                    sum += this.data[i][k] * other.data[k][j];
                }
                result.data[i][j] = sum;
            }
        }
        return result;
    }

    // Matrix subtraction
    public Matrix subtract(Matrix other) {
        if (this.rows != other.rows || this.cols != other.cols) {
            throw new IllegalArgumentException("Matrices must have the same dimensions for subtraction.");
        }
        Matrix result = new Matrix(this.rows, this.cols);
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                result.data[i][j] = this.data[i][j] - other.data[i][j];
            }
        }
        return result;
    }

    // Frobenius norm
    public double frobeniusNorm() {
        double sum = 0;
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                sum += data[i][j] * data[i][j];
            }
        }
        return Math.sqrt(sum);
    }

    // Print matrix
    public void print(BufferedWriter writer) throws IOException {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                writer.write(String.format("%.4f ", data[i][j]));
            }
            writer.newLine();
        }
    }
}
