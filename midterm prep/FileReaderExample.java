import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileReaderExample {
    // Method to read and print each line from a file without try-catch blocks
    public static void readFileAndPrint() throws IOException {
        // Open the file using BufferedReader
        BufferedReader br = new BufferedReader(new FileReader("data.txt"));
        
        String line;
        // Read each line until the end of the file
        while ((line = br.readLine()) != null) {
            System.out.println(line); // Print the current line to the console
        }
        
        // Close the BufferedReader
        br.close();
    }

    // Main method to test the file reading method
    public static void main(String[] args) throws IOException {
        readFileAndPrint(); // Call the method to read and print the file
    }
}

    
