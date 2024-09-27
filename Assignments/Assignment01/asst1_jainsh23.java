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
    private static void displayWelcomeMessage() {
        System.out.println("WELCOME TO THE SPRING WEIGHT CALCULATOR (0 TO QUIT, 1 TO PROCEED)");
    }

    // Method to perform the calculation
    private static void performCalculation(Scanner scanner) {
        //set the bounds for the inputs
        double D = getValidInput(scanner, "Enter coil diameter D (m): ", 0.25, 1.3, Double.class); 
        double d = getValidInput(scanner, "Enter wire diameter d (m): ", 0.05, 2.0, Double.class);
        int N = getValidInput(scanner, "Enter number of turns N: ", 1, 15, Integer.class);

        double weight = calculateSpringWeight(D, d, N); // Calculate the spring weight
        System.out.printf("Weight: %.2f kgm/s^2%n", weight);
    }

    // Generalized method to get valid input within specified bounds
    public static <T extends Number> T getValidInput(Scanner scanner, String prompt, double minValue, double maxValue, Class<T> type) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                if (type == Double.class) {
                    double value = Double.parseDouble(input);
                    if (value <= 0) {
                        System.out.println("ENTER A POSITIVE INPUT");
                    } else if (value >= minValue && value <= maxValue) {
                        return type.cast(value); // Return the valid input
                    } else {
                        System.out.println("INPUT MUST BE WITHIN BOUNDS");
                    }
                } else if (type == Integer.class) {
                    double tempValue = Double.parseDouble(input); // Parse as double to check for decimals
                    if (tempValue <= 0) {
                        System.out.println("N SHOULD BE A POSITIVE INTEGER");
                    } else if (tempValue != Math.floor(tempValue)) {
                        System.out.println("N SHOULD BE AN INTEGER");
                    } else {
                        int value = (int) tempValue;
                        if (value >= (int) minValue && value <= (int) maxValue) {
                            return type.cast(value); // Return the valid input
                        } else {
                            System.out.println("INPUT MUST BE WITHIN BOUNDS");
                        }
                    }
                }
            } catch (NumberFormatException e) {
                if (type == Integer.class) {
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