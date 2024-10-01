package Assignments.Assignment01;

import java.util.Scanner;

public class asst1_jainsh23 {

    // Constant for gravity
    private static final double GRAVITY = 9.81;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // Create a scanner object
        displayWelcomeMessage(); // Display welcome message

        while (true) {
            System.out.print("Enter 0 to quit or 1 to proceed: ");
            String choice = scanner.nextLine();

            if (choice.equals("0")) {
                break; // Exit the loop if choice is 0
            } else if (choice.equals("1")) {
                performCalculation(scanner); // Perform the calculation
                System.out.println("GOODBYE!");
                scanner.close(); // Close the scanner
                return; // Exit the program after calculation
            } else {
                System.out.println("ENTER A VALID INPUT");
            }
        }

        System.out.println("GOODBYE!");
        scanner.close();
    }

    // Method to display the welcome message
    private static void displayWelcomeMessage() { // private since it is only used in this class
        System.out.println("WELCOME TO THE SPRING WEIGHT CALCULATOR (0 TO QUIT, 1 TO PROCEED)");
    }

    // Method to perform the calculation
    private static void performCalculation(Scanner scanner) {
        // Set the bounds for the inputs
        double D = getValidInput(scanner, "Enter coil diameter D (m): ", 0.25, 1.3, false);
        double d = getValidInput(scanner, "Enter wire diameter d (m): ", 0.05, 2.0, false);
        int N = (int) getValidInput(scanner, "Enter number of turns N: ", 1, 15, true);

        double weight = calculateSpringWeight(D, d, N); // Calculate the spring weight
        double truncatedWeight = Math.floor(weight * 100) / 100.0; // Truncate the weight to 2 decimal places
        System.out.printf("Weight: %.2f kgm/s^2%n", truncatedWeight);
    }

    // Method to get valid input within specified bounds (handles both integers and doubles)
    public static double getValidInput(Scanner scanner, String prompt, double minValue, double maxValue, boolean isInteger) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                double value = Double.parseDouble(input);
                if (value <= 0) {
                    if (isInteger) {
                        System.out.println("N SHOULD BE A POSITIVE INTEGER");
                    } else {
                        System.out.println("ENTER A POSITIVE INPUT");
                    }
                } else if (value < minValue || value > maxValue) {
                    System.out.println("INPUT MUST BE WITHIN BOUNDS");
                } else if (isInteger && value != Math.floor(value)) {
                    System.out.println("N SHOULD BE AN INTEGER");
                } else {
                    return value; // Return the valid input
                }
            } catch (NumberFormatException e) {
                if (isInteger) {
                    System.out.println("N SHOULD BE AN INTEGER");
                } else {
                    System.out.println("ENTER A VALID INPUT");
                }
            }
        }
    }

    // Method to calculate spring weight
    public static double calculateSpringWeight(double D, double d, int N) {
        double mass = (N + 2) * D * Math.pow(d, 2);
        return mass * GRAVITY; // Return the weight (display result in kgm/s^2)
    }
}
