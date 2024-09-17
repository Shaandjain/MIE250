package Lab.Assignment01;

import java.util.Scanner;

public class SpringWeightCalculator {

    // Method to get valid input for double values
    public static double getValidDoubleInput(Scanner scanner, String prompt, double minValue, double maxValue) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine());
                if (value <= 0) {
                    System.out.println("ENTER A POSITIVE INPUT");
                } else if (value < minValue || value > maxValue) { //using max/min values set in the main method (in the prompt)
                    System.out.println("INPUT MUST BE WITHIN BOUNDS"); 
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("ENTER A VALID INPUT");
            }
        }
    }

    // Method to get valid input for integer values
    public static int getValidIntegerInput(Scanner scanner, String prompt, int minValue, int maxValue) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine());
                if (value <= 0) {
                    System.out.println("N SHOULD BE A POSITIVE INTEGER");
                } else if (value < minValue || value > maxValue) {
                    System.out.println("INPUT MUST BE WITHIN BOUNDS");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("N SHOULD BE AN INTEGER");
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("WELCOME TO THE SPRING WEIGHT CALCULATOR (0 TO QUIT, 1 TO PROCEED)");

        while (true) {
            System.out.print("Enter 0 to quit or 1 to proceed: "); // Prompt user for choice
            String choice = scanner.nextLine();

            if (choice.equals("0")) {
                System.out.println("GOODBYE!"); // Exit the program
                System.exit(0); // Terminate the program
            } else if (choice.equals("1")) {
                // Get valid inputs for D, d, and N
                double D = getValidDoubleInput(scanner, "Enter coil diameter D (m): ", 0.25, 1.3);
                double d = getValidDoubleInput(scanner, "Enter wire diameter d (m): ", 0.05, 2.0);
                int N = getValidIntegerInput(scanner, "Enter number of turns N: ", 1, 15);

                // Compute mass and weight
                double m = (N + 2) * D * Math.pow(d, 2); // Mass
                double g = 9.81;  // Acceleration due to gravity
                double w = m * g; // Weight

                // Display result
                System.out.printf("Weight: %.2f kgm/s^2%n", w);
                System.out.println("GOODBYE!");
                System.exit(0); // Terminate the program
            } else {
                System.out.println("ENTER A VALID INPUT");
            }
        }
    }
}