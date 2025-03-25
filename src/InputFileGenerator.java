import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;

public class InputFileGenerator {
    private static final String BASES = "ACGT";
    private static final int LINE_LENGTH = 500;
    private static final int TOTAL_LINES = 10;
    private static final String KMER = "GTCTACGGC";


    public static void main(String[] args) {
        //dna string to be used
        StringBuilder dna = new StringBuilder();

        //Create pure string
        for (int i = 0; i < TOTAL_LINES; i++) {
            dna.append(generateRandomString(LINE_LENGTH)).append("\n");
        }

        //create k-mer, add mutations to k-mer
        StringBuilder[] kmers = createKMERS();

        System.out.println("Inserted (mutated) Kmers:");
        System.out.println(Arrays.toString(kmers));
        //inject into dna string

        //Write to file
        try (FileWriter writer = new FileWriter("input.txt")) {
            writer.write(dna.toString());
            //System.out.println("Input file generated successfully.");
        } catch (IOException e) {
            //System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    private static void mutate(StringBuilder[] kmers, int mutationCount) {
        SecureRandom random = new SecureRandom();

        for(int j = mutationCount; j > 0; j--) {
            for (int i = 0; i < TOTAL_LINES; i++) {
                int random_i = random.nextInt(KMER.length());
                char random_base = BASES.charAt(random.nextInt(BASES.length()));
                char reference_base = kmers[i].charAt(random_i);

                while (random_base == reference_base)
                    random_base = BASES.charAt(random.nextInt(BASES.length()));

                kmers[i].setCharAt(random_i, random_base);
            }
        }

        //System.out.println("\n inside mutate: \n");
        //System.out.println(Arrays.toString(kmers));
    }

    private static StringBuilder[] createKMERS() {
        StringBuilder[] kmers = new StringBuilder[TOTAL_LINES];
        for(int i = 0; i < TOTAL_LINES; i++)
            kmers[i] = new StringBuilder(KMER);

        //System.out.println(Arrays.toString(kmers));

        //Mutate kmers
        mutate(kmers, 4);

        //System.out.println("\n after mutate: \n");
        //System.out.println(Arrays.toString(kmers));

        return kmers;
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
