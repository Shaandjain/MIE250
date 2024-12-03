import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.io.BufferedWriter;
import java.io.IOException;



public class asst3_jains23 {
    public static void main(String[] args) {
        String inputFileName = "input.txt";
        String outputFileName = "output.txt";
        String executionMode = "parallel"; // default to parallel
        boolean noInputFileSpecified = false;

        // Check command-line arguments
        if (args.length >= 1) {
            inputFileName = args[0];
        } else {
            noInputFileSpecified = true;
        }
        // input and output file paths
        File inputFile = new File(inputFileName);
        File outputFile = new File(outputFileName);
        // Read execution mode from config.txt
        try {
            executionMode = readConfig("config.txt");
        } catch (Exception e) {
            // If config.txt cannot be read, default to "parallel"
            executionMode = "parallel";
        }

        // Prepare to write output to output.txt
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            // Write header
            if (noInputFileSpecified) {
                writer.write("No input file specified. Using default: input.txt\n");
            }
            writer.write("Input file: " + inputFile + "\n");
            writer.write("Output file: " + outputFile + "\n");
            writer.write("Execution mode: " + executionMode + "\n\n");

            // Read matrix A from input file
            Matrix A = null;
            try {
                A = readMatrix(inputFile);
            } catch (Exception e) {
                // Write error message to output file
                writer.write("Error: " + e.getMessage() + "\n");
                writer.close();
                return;
            }

            // Write Matrix A
            writer.write("Matrix A:\n");
            A.write(writer);
            writer.write("\n");

            // Check if A is square
            if (A.getRows() != A.getCols()) {
                writer.write("Error: Matrix must be square.\n");
                writer.close();
                return;
            }

            // Perform LU decomposition
            LUDecomposer decomposer = new LUDecomposer(A, executionMode.equalsIgnoreCase("parallel"));
            try {
                decomposer.decompose();
            } catch (Exception e) {
                // Handle errors such as singular matrix
                writer.write("Error: " + e.getMessage() + "\n");
                writer.close();
                return;
            }

            // Get L and U matrices
            Matrix L = decomposer.getL();
            Matrix U = decomposer.getU();

            // Compute difference matrix D = A - L * U
            Matrix LU = L.multiply(U);
            Matrix D = A.subtract(LU);
            double tolerance = D.frobeniusNorm();

            // Write matrices to output file
            writer.write("Final Matrix L:\n");
            L.write(writer);
            writer.write("\n");

            writer.write("Final Matrix U:\n");
            U.write(writer);
            writer.write("\n");

            writer.write("Difference Matrix (A - LU):\n");
            D.write(writer);
            writer.write("\n");

            writer.write("Tolerance (difference between A and LU): " + String.format("%.4f", tolerance) + "\n");
            writer.write("Decomposition complete. Results written to " + outputFile + "\n");

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to read config.txt
    private static String readConfig(String configFileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(configFileName));
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("parallel_execution=")) {
                String value = line.substring("parallel_execution=".length()).trim();
                br.close();
                return value.equalsIgnoreCase("true") ? "parallel" : "sequential";
            }
        }
        br.close();
        return "parallel"; // default
    }

    // Method to read matrix from input file
    private static Matrix readMatrix(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        List<double[]> data = new ArrayList<>();
        String line;
        int n = -1;
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue; // Skip empty lines
            String[] tokens = line.trim().split("\\s+");
            if (n == -1) {
                n = tokens.length;
            } else if (tokens.length != n) {
                br.close();
                throw new IllegalArgumentException("All rows must have the same number of columns.");
            }
            double[] row = new double[n];
            for (int i = 0; i < n; i++) {
                try {
                    row[i] = Double.parseDouble(tokens[i]);
                } catch (NumberFormatException e) {
                    br.close();
                    throw new IllegalArgumentException("Invalid number format in matrix.");
                }
            }
            data.add(row);
        }
        br.close();
        if (data.size() != n) {
            throw new IllegalArgumentException("Matrix must be square.");
        }
        double[][] matrixData = new double[n][n];
        for (int i = 0; i < n; i++) {
            matrixData[i] = data.get(i);
        }
        return new Matrix(matrixData);
    }
    private static Matrix readMatrix(File file) throws IOException {
        return readMatrix(file.getPath()); // Call the existing readMatrix(String) method
    }
}

class LUDecomposer {
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
            List<Future<Void>> futures = new ArrayList<>();
    
            for (int j = i; j < n; j++) {
                final int col = j;
                Future<Void> future = executor.submit(() -> {
                    double sum = 0;
                    for (int k = 0; k < row; k++) {
                        sum += L.get(row, k) * U.get(k, col);
                    }
                    U.set(row, col, A.get(row, col) - sum);
                    return null; // Since the task doesn't return a value, return null explicitly.
                });
                futures.add(future);
            }
    
            // Wait for U[i][*] computation to finish
            for (Future<Void> future : futures) {
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
            final int colI = i;
    
            // Compute L[j][i]
            for (int j = i + 1; j < n; j++) {
                final int rowJ = j;
                Future<Void> future = executor.submit(() -> {
                    double sum = 0;
                    for (int k = 0; k < colI; k++) {
                        sum += L.get(rowJ, k) * U.get(k, colI);
                    }
                    L.set(rowJ, colI, (A.get(rowJ, colI) - sum) / U.get(colI, colI));
                    return null; // Since the task doesn't return a value, return null explicitly.
                });
                futures.add(future);
            }
    
            // Wait for L[*][i] computation to finish
            for (Future<Void> future : futures) {
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

class Matrix {
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
