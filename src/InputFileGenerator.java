import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;

public class InputFileGenerator {
    private static final String BASES = "ACGT";
    private static final int LINE_LENGTH = 500;
    private static final int TOTAL_LINES = 10;

    public static void main(String[] args) {
        try (FileWriter writer = new FileWriter("input.txt")) {
            for (int i = 0; i < TOTAL_LINES; i++) {
                writer.write(generateRandomString(LINE_LENGTH) + "\n");
            }
            System.out.println("Input file generated successfully.");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    private static String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(BASES.charAt(random.nextInt(BASES.length())));
        }
        return sb.toString();
    }
}
