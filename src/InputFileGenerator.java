import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;

public class InputFileGenerator {
    private static final String BASES = "ACGT";
    protected static final int LINE_LENGTH = 500;
    protected static final int TOTAL_LINES = 10;
    private static final String KMER = "GTCTACGGC";
    private static final int MutationCount = 4;


    public static void main(String[] args) {
        //dna string to be used
        StringBuilder dna = new StringBuilder();

        //create k-mer, add mutations to k-mer
        StringBuilder[] kmers = createKMERS();


        System.out.print("\nInserted (Original) Kmer: ");
        System.out.println(KMER+"\n");
        System.out.println("\nInserted (mutated) Kmers:");
        System.out.println(Arrays.toString(kmers)+"\n");

        //create dna string, inject kmers
        createDNA(kmers, dna);


        //Write to file
        try (FileWriter writer = new FileWriter("dna.txt")) {
            writer.write(dna.toString());
            //System.out.println("Input file generated successfully.");
        } catch (IOException e) {
            //System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    private static void createDNA(StringBuilder[] kmers, StringBuilder dna) {
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < TOTAL_LINES; i++) {
            StringBuilder line = new StringBuilder(generateRandomString(LINE_LENGTH));
            int replacement_index = random.nextInt(LINE_LENGTH-KMER.length());

            line.replace(replacement_index, replacement_index + KMER.length(), kmers[i].toString());
            dna.append(line).append("\n");
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
        mutate(kmers, MutationCount);

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
