import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
        // Gradient for the 2D case 
        for (int i = 0; i < variables.length; i++) {
            gradient[i] = 0;
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

// New class for the bonus functionality -- easter egg, enjoy:)
class RosenbrockBonusFunction extends ObjectiveFunction {
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
        int n = variables.length;
        double[] gradient = new double[n];

        // Compute gradient for general d-dimensional case
        for (int i = 0; i < n; i++) {
            if (i == 0) {
                // First component
                gradient[i] = -400 * variables[i] * (variables[i + 1] - variables[i] * variables[i])
                        - 2 * (1 - variables[i]);
            } else if (i == n - 1) {
                // Last component
                gradient[i] = 200 * (variables[i] - variables[i - 1] * variables[i - 1]);
            } else {
                // Middle components
                gradient[i] = 200 * (variables[i] - variables[i - 1] * variables[i - 1])
                        - 400 * variables[i] * (variables[i + 1] - variables[i] * variables[i])
                        - 2 * (1 - variables[i]);
            }
        }
        return gradient;
    }

    @Override
    double[] getBounds() {
        return new double[] { -5.0, 5.0 };
    }

    @Override
    String getName() {
        return "Rosenbrock_Bonus";
    }
}

public class asst2_jainsh23 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            int choice = getValidatedInput(scanner, "Press 0 to exit or 1 to enter the program:", 0, 1);
            if (choice == 0) {
                System.out.println("Exiting program...");
                break;
            }

            int inputChoice = getValidatedInput(scanner, "Press 0 for .txt input or 1 for manual input:", 0, 1);

            ObjectiveFunction objectiveFunction = null;
            int dimensionality = 0;
            int iterations = 0;
            double tolerance = 0;
            double stepSize = 0;
            double[] startPoint = null;
            int outputChoice = 0;
            String outputPath = "";

            outputChoice = getValidatedInput(scanner, "Press 0 for .txt output or 1 for console output:", 0, 1);

            if (inputChoice == 1) {
                // Manual input
                objectiveFunction = getManualObjectiveFunction(scanner);
                if (objectiveFunction == null) {
                    System.out.println("Error: Unknown objective function.");
                    continue;
                }

                dimensionality = getValidatedInput(scanner, "Enter the dimensionality of the problem:", 1,
                        Integer.MAX_VALUE);
                iterations = getValidatedInput(scanner, "Enter the number of iterations:", 1, Integer.MAX_VALUE);
                tolerance = getValidatedDoubleInput(scanner, "Enter the tolerance:", 0, Double.MAX_VALUE);
                stepSize = getValidatedDoubleInput(scanner, "Enter the step size:", 0, Double.MAX_VALUE);

                System.out.println("Enter the initial point as " + dimensionality + " space-separated values:");
                startPoint = new double[dimensionality];
                for (int i = 0; i < dimensionality; i++) {
                    startPoint[i] = scanner.nextDouble();
                    startPoint[i] = floorTo5Decimals(startPoint[i]);
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
                        startPoint[i] = floorTo5Decimals(startPoint[i]);
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
                System.out.printf("Error: Initial point is outside the bounds [%.1f, %.1f].\n", bounds[0],
                        bounds[1]);
                continue;
            }

            StringBuilder output = new StringBuilder();
            output.append("Objective Function: ").append(objectiveFunction.getName()).append("\n");
            output.append("Dimensionality: ").append(dimensionality).append("\n");
            output.append("Initial Point:");
            for (double v : startPoint) {
                output.append(" ").append(String.format("%.5f", v));
            }
            output.append("\nIterations: ").append(iterations).append("\n");
            output.append("Tolerance: ").append(String.format("%.5f", tolerance)).append("\n");
            output.append("Step Size: ").append(String.format("%.5f", stepSize)).append("\n");
            output.append("Optimization process:\n");

            int iterationsPerformed = optimizeSteepestDescent(objectiveFunction, startPoint, iterations, tolerance,
                    stepSize, output);

            if (iterationsPerformed < iterations) {
                output.append("Convergence reached after ").append(iterationsPerformed).append(" iterations.\n");
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
            case "rosenbrock_bonus": // Hidden option
                return new RosenbrockBonusFunction(); // Easter egg ;)
            default:
                return null;
        }
    }

    private static ObjectiveFunction getFileObjectiveFunction(Scanner fileScanner) {
        String functionName = fileScanner.next().toLowerCase();
        switch (functionName) {
            case "quadratic":
                return new QuadraticFunction();
            case "rosenbrock":
                return new RosenbrockFunction();
            case "rosenbrock_bonus": // Hidden option
                return new RosenbrockBonusFunction();
            default:
                return null;
        }
    }

    private static int getValidatedInput(Scanner scanner, String prompt, int min, int max) {
        int value;
        System.out.println(prompt);
        while (true) {
            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("Please enter a valid input (" + min + " or " + max + ").");
                }
            } else {
                scanner.next();
                System.out.println("Please enter a valid input (" + min + " or " + max + ").");
            }
        }
    }

    private static double getValidatedDoubleInput(Scanner scanner, String prompt, double min, double max) {
        double value;
        System.out.println(prompt);
        while (true) {
            if (scanner.hasNextDouble()) {
                value = scanner.nextDouble();
                if (value > min && value <= max) {
                    return value;
                } else {
                    System.out.println("Please enter a valid positive number.");
                }
            } else {
                scanner.next();
                System.out.println("Please enter a valid positive number.");
            }
        }
    }

    private static boolean checkBounds(double[] variables, double[] bounds) {
        for (double v : variables) {
            if (v < bounds[0] || v > bounds[1]) {
                System.out.printf("Error: Initial point %.5f is outside the bounds [%.1f, %.1f].\n", v, bounds[0],
                        bounds[1]);
                return false;
            }
        }
        return true;
    }

    private static int optimizeSteepestDescent(ObjectiveFunction objectiveFunction, double[] variables,
            int maxIterations, double tolerance, double stepSize, StringBuilder output) {

        double[] x = variables.clone();
        int iteration = 1; // Start from 1 to match the output iteration numbering
        double prevGradNorm = 0.0;
        boolean converged = false;

        while (iteration <= maxIterations) {
            double funcValue = objectiveFunction.compute(x);
            double[] grad = objectiveFunction.computeGradient(x);
            double gradNorm = norm(grad);

            // Floor funcValue and gradNorm
            funcValue = floorTo5Decimals(funcValue);
            gradNorm = floorTo5Decimals(gradNorm);

            // Output the results
            output.append("Iteration ").append(iteration).append(":\n");
            output.append("Objective Function Value: ").append(String.format("%.5f", funcValue)).append("\n");
            output.append("x-values:");
            for (double xi : x) {
                output.append(" ").append(String.format("%.5f", xi));
            }
            output.append("\n");

            // For iteration > 1, print "Current Tolerance" from previous gradient norm
            if (iteration > 1) {
                output.append("Current Tolerance: ").append(String.format("%.5f", prevGradNorm)).append("\n");
            }

            // Check for convergence using prevGradNorm
            if (iteration > 1 && prevGradNorm < tolerance) {
                converged = true;
                break;
            }

            // Update variables and floor them
            for (int i = 0; i < x.length; i++) {
                x[i] = x[i] - stepSize * grad[i];
                x[i] = floorTo5Decimals(x[i]);
            }

            // Update prevGradNorm for the next iteration
            prevGradNorm = gradNorm;

            iteration++; // Increment iteration at the end
        }

        // Return the number of iterations actually performed (converged or maxIterations)
        if (converged) {
            return iteration;
        } else {
            return maxIterations;
        }
    }

    private static double norm(double[] vec) {
        double sum = 0;
        for (double v : vec) {
            sum += v * v;
        }
        return Math.sqrt(sum);
    }

    // Flooring to exactly 5 decimal places using BigDecimal -- from announcement
    private static double floorTo5Decimals(double value) {
        BigDecimal bd = new BigDecimal(value).setScale(5, RoundingMode.DOWN);
        return bd.doubleValue();
    }
}
