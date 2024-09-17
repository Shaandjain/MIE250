
package Lab.Lab0;

public class setup {
    public static void main(String[] args) {
        System.out.println("Hello, World!");

        int result = fibonacci(10); // Replace 10 with the desired value of n
        System.out.println("Fibonacci result: " + result);
    }

    public static int fibonacci(int n) {
        if (n <= 1) {
            return n;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
}


