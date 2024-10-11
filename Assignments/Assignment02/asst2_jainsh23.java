package Assignments.Assignment02;
import java.util.Scanner;

//this program takes the input from a user
//for the type of function they want to optimize
//and asks for the type of function (quadratic or Rosenbrock),
//the dimensionality, the number of iterations, the step size,
//the tolerance, and the initial point with boundaries [-5.0, 5.0]

public class asst2_jainsh23 {
    
    public abstract class ObjectiveFunction {
        public abstract double compute(double[] variables);
        
        public abstract double[] computeGradient(double[] variables);
        
        public abstract double[] getBounds();
        
        public abstract String getName();
    }

    public class SteepestDescentOptimizer {
        public void optimizeSteepestDescent(ObjectiveFunction objectiveFunction, double[] variables, int iterations, double tolerance, double stepSize, int dimensionality) {
            // Implementation of the Steepest Descent optimization method
            // ...
        }

        public String getValidatedInput(Scanner scanner, String prompt) {
            String input = "";
            // Validate user input
            // ...
            return input;
        }

        public String getManualInput(Scanner scanner){
            String input = "";
            // Get manual input
            // ...
            return input;
        }

        public String getFileInput(Scanner scanner){
            String input = "";
            // Get file input
            // ...
            return input;
        }

        public Double checkBounds(double[] variables, double[] bounds ){
            double value = 0.0;
            // Check if variables are within bounds
            // ...
            return value;
        }
    }
}
