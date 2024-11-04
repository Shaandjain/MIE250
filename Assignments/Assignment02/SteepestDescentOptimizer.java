package Assignments.Assignment02;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

abstract class ObjectiveFunction {
    abstract double compute(double[] variables);
    abstract double[] computeGradient(double[] variables);
    abstract double[] getBounds();
    abstract String getName();
}

class QuadraticFunction extends ObjectiveFunction {
    @Override
    double compute(double[] variables) {
        double sum = 0;
        for (double v : variables) {
            sum += v * v;
        }
        return sum;
    }

    @Override
    double[] computeGradient(double[] variables) {
        double[] gradient = new double[variables.length];
        for (int i = 0; i < variables.length; i++) {
            gradient[i] = 2 * variables[i];
        }
        return gradient;
    }

    @Override
    double[] getBounds() {
        return new double[] { -5.0, 5.0 };
    }

    @Override
    String getName() {
        return "Quadratic";
    }
}

class RosenbrockFunction extends ObjectiveFunction {
    @Override
    double compute(double[] variables) {
        double sum = 0;
        for (int i = 0; i < variables.length - 1; i++) {
            sum += 100 * Math.pow(variables[i + 1] - variables[i] * variables[i], 2)
                    + Math.pow(1 - variables[i], 2);
        }
        return sum;
    }

    @Override
    double[] computeGradient(double[] variables) {
        double[] gradient = new double[variables.length];
        for (int i = 0; i < variables.length; i++) {
            gradient[i] = 0; // Initialize gradient components
        }
        for (int i = 0; i < variables.length - 1; i++) {
            gradient[i] += -400 * variables[i] * (variables[i + 1] - variables[i] * variables[i])
                    - 2 * (1 - variables[i]);
            gradient[i + 1] += 200 * (variables[i + 1] - variables[i] * variables[i]);
        }
        return gradient;
    }

    @Override
    double[] getBounds() {
        return new double[] { -5.0, 5.0 };
    }

    @Override
    String getName() {
        return "Rosenbrock";
    }
}

public class SteepestDescentOptimizer {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Press 0 to exit or 1 to enter the program:");
            int choice = getValidatedInput(scanner, "Please enter a valid input (0 or 1).", 0, 1);
            if (choice == 0) {
                System.out.println("Exiting program...");
                break;
            }

            System.out.println("Press 0 for .txt input or 1 for manual input:");
            int inputChoice = getValidatedInput(scanner, "Please enter a valid input (0 or 1).", 0, 1);

            ObjectiveFunction objectiveFunction = null;
            int dimensionality = 0;
            int iterations = 0;
            double tolerance = 0;
            double stepSize = 0;
            double[] startPoint = null;
            int outputChoice = 0;
            String outputPath = "";

            System.out.println("Press 0 for .txt output or 1 for console output:");
            outputChoice = getValidatedInput(scanner, "Please enter a valid input (0 or 1).", 0, 1);

            if (inputChoice == 1) {
                // Manual input
                objectiveFunction = getManualObjectiveFunction(scanner);
                if (objectiveFunction == null) {
                    System.out.println("Error: Unknown objective function.");
                    continue;
                }

                dimensionality = getValidatedInput(scanner, "Enter the dimensionality of the problem (positive integer):", 1, Integer.MAX_VALUE);
                iterations = getValidatedInput(scanner, "Enter the number of iterations (positive integer):", 1, Integer.MAX_VALUE);
                tolerance = getValidatedDoubleInput(scanner, "Enter the tolerance (positive number):", 0, Double.MAX_VALUE);
                stepSize = getValidatedDoubleInput(scanner, "Enter the step size (positive number):", 0, Double.MAX_VALUE);

                System.out.println("Enter the initial point as " + dimensionality + " space-separated values:");
                startPoint = new double[dimensionality];
                for (int i = 0; i < dimensionality; i++) {
                    startPoint[i] = scanner.nextDouble();
                }
            } else {
                // File input
                System.out.println("Please provide the path to the config file:");
                String filePath = scanner.next();
                try {
                    Scanner fileScanner = new Scanner(new File(filePath));
                    objectiveFunction = getFileObjectiveFunction(fileScanner);
                    if (objectiveFunction == null) {
                        System.out.println("Error: Unknown objective function.");
                        continue;
                    }
                    dimensionality = fileScanner.nextInt();
                    iterations = fileScanner.nextInt();
                    tolerance = fileScanner.nextDouble();
                    stepSize = fileScanner.nextDouble();
                    startPoint = new double[dimensionality];
                    for (int i = 0; i < dimensionality; i++) {
                        startPoint[i] = fileScanner.nextDouble();
                    }
                    fileScanner.close();
                } catch (FileNotFoundException e) {
                    System.out.println("Error reading the file.");
                    continue;
                } catch (Exception e) {
                    System.out.println("Error: Invalid file format.");
                    continue;
                }
            }

            double[] bounds = objectiveFunction.getBounds();
            if (!checkBounds(startPoint, bounds)) {
                System.out.printf("Error: Initial point is outside the bounds [%.1f, %.1f].\n", bounds[0], bounds[1]);
                continue;
            }

            StringBuilder output = new StringBuilder();
            output.append("Objective Function: ").append(objectiveFunction.getName()).append("\n");
            output.append("Dimensionality: ").append(dimensionality).append("\n");
            output.append("Initial Point: ");
            for (double v : startPoint) {
                output.append(String.format("%.5f ", v));
            }
            output.append("\nIterations: ").append(iterations).append("\n");
            output.append("Tolerance: ").append(String.format("%.5f", tolerance)).append("\n");
            output.append("Step Size: ").append(String.format("%.5f", stepSize)).append("\n");
            output.append("Optimization process:\n");

            boolean converged = optimizeSteepestDescent(objectiveFunction, startPoint, iterations, tolerance, stepSize, output);

            if (converged) {
                output.append("Convergence reached after ").append(iterations).append(" iterations.\n");
            } else {
                output.append("Maximum iterations reached without satisfying the tolerance.\n");
            }
            output.append("Optimization process completed.\n");

            if (outputChoice == 1) {
                // Console output
                System.out.println(output.toString());
            } else {
                // File output
                System.out.println("Please provide the path for the output file:");
                outputPath = scanner.next();
                try {
                    FileWriter writer = new FileWriter(outputPath);
                    writer.write(output.toString());
                    writer.close();
                    System.out.println("Results written to " + outputPath);
                } catch (IOException e) {
                    System.out.println("Error writing to the file.");
                }
            }
        }
        scanner.close();
    }

    private static ObjectiveFunction getManualObjectiveFunction(Scanner scanner) {
        System.out.println("Enter the choice of objective function (quadratic or rosenbrock):");
        String functionName = scanner.next().toLowerCase();
        switch (functionName) {
            case "quadratic":
                return new QuadraticFunction();
            case "rosenbrock":
                return new RosenbrockFunction();
            default:
                return null;
        }
    }

    private static ObjectiveFunction getFileObjectiveFunction(Scanner fileScanner) {
        String functionName = fileScanner.nextLine().toLowerCase();
        switch (functionName) {
            case "quadratic":
                return new QuadraticFunction();
            case "rosenbrock":
                return new RosenbrockFunction();
            default:
                return null;
        }
    }

    private static int getValidatedInput(Scanner scanner, String prompt, int min, int max) {
        int value;
        while (true) {
            System.out.println(prompt);
            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                if (value >= min && value <= max) {
                    return value;
                }
            } else {
                scanner.next(); // Consume invalid input
            }
            System.out.println("Please enter a valid input (" + min + " or " + max + ").");
        }
    }

    private static double getValidatedDoubleInput(Scanner scanner, String prompt, double min, double max) {
        double value;
        while (true) {
            System.out.println(prompt);
            if (scanner.hasNextDouble()) {
                value = scanner.nextDouble();
                if (value > min && value <= max) {
                    return value;
                }
            } else {
                scanner.next(); // Consume invalid input
            }
            System.out.println("Please enter a valid positive number.");
        }
    }

    private static boolean checkBounds(double[] variables, double[] bounds) {
        for (double v : variables) {
            if (v < bounds[0] || v > bounds[1]) {
                System.out.printf("Error: Initial point %.5f is outside the bounds [%.1f, %.1f].\n", v, bounds[0], bounds[1]);
                return false;
            }
        }
        return true;
    }

    private static boolean optimizeSteepestDescent(ObjectiveFunction objectiveFunction, double[] variables,
            int maxIterations, double tolerance, double stepSize, StringBuilder output) {

        double[] x = variables.clone();
        int iteration = 0;
        boolean converged = false;

        while (iteration < maxIterations) {
            double funcValue = objectiveFunction.compute(x);
            double[] grad = objectiveFunction.computeGradient(x);
            double gradNorm = norm(grad);

            output.append("Iteration ").append(iteration + 1).append(":\n");
            output.append("Objective Function Value: ").append(String.format("%.5f", funcValue)).append("\n");
            output.append("x-values:");
            for (double xi : x) {
                output.append(" ").append(String.format("%.5f", xi));
            }
            output.append("\n");
            output.append("Current Tolerance: ").append(String.format("%.5f", gradNorm)).append("\n");

            if (gradNorm < tolerance) {
                converged = true;
                break;
            }

            for (int i = 0; i < x.length; i++) {
                x[i] = x[i] - stepSize * grad[i];
                x[i] = Math.floor(x[i] * 100000) / 100000; // Floor to exactly 5 digits
            }

            iteration++;
        }
        return converged;
    }

    private static double norm(double[] vec) {
        double sum = 0;
        for (double v : vec) {
            sum += v * v;
        }
        return Math.sqrt(sum);
    }
}
