package Assignments.Assignment03;

import java.io.*;
import java.util.*;


public class LUFactorization {
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
