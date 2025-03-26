import java.io.File;
import java.io.FileNotFoundException;
import java.security.SecureRandom;
import java.util.*;

public class Main {

    private static final int A = 0;
    private static final int C = 1;
    private static final int G = 2;
    private static final int T = 3;

    public static void main(String[] args) {
        String[] bestMotifs = new String[InputFileGenerator.TOTAL_LINES];
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter consensus string length: ");
        int consensusStringLen = scanner.nextInt();

        //Generate dna
        InputFileGenerator.main(new String[0]);
        //Read generated dna
        String[] sequences = readDNAFile();

        //Generate random motifs
        String[] motifs = initiateMotifs(sequences , consensusStringLen);

        bestMotifs = motifs.clone();

        System.out.println("initial motifs: " + Arrays.toString(bestMotifs));


        //REPEAT UNTIL CONVERGENCE

        float[][] profileMatrix = createProfileMatrix(motifs);

        System.out.println("\n" + Arrays.deepToString(profileMatrix));

        motifs = setMotifs(sequences, profileMatrix, consensusStringLen);

        System.out.println("\n" + Arrays.toString(motifs));

        //calculateMotifScores();
    }

    private static String[] setMotifs(String[] sequences, float[][] profileMatrix, int consensusStringLen) {
        String subString;
        String[] bestSubStrings = new String[InputFileGenerator.TOTAL_LINES];

        //find best substring(motif) from profile matrix
        for(int i = 0; i < InputFileGenerator.TOTAL_LINES; i++) {
            float bestProb = 0;
            String bestSubString = "";

            for (int j = 0; j < InputFileGenerator.LINE_LENGTH - consensusStringLen; j++) {
                subString = sequences[i].substring(j, j + consensusStringLen);

                float prob = calculateSubStringProb(subString, profileMatrix);

                //update the best probability and best motif found
                if(prob > bestProb) {
                    bestProb = prob;
                    bestSubString = subString;
                }
            }

            bestSubStrings[i] = bestSubString;
        }

        return bestSubStrings;
    }

    private static float calculateSubStringProb(String subString, float[][] profileMatrix) {
        float prob = 1;

        for(int i = 0; i < profileMatrix[A].length; i++) {
            switch (subString.charAt(i)) {
                case 'A':
                    prob *= profileMatrix[A][i];
                    break;
                case 'C':
                    prob *= profileMatrix[C][i];
                    break;
                case 'G':
                    prob *= profileMatrix[G][i];
                    break;
                case 'T':
                    prob *= profileMatrix[T][i];
                    break;
            }
        }

        return prob;
    }

    private static float[][] createProfileMatrix(String[] motifs) {
        //Stores profile matrix in the form:
        //A[]-->2, 1 ...
        //C[]-->4, 2 ...
        //G[]-->1, 4 ...
        //T[]-->3, 3 ...
        float[][] profileMatrix = new float[4][motifs[0].length()];

        int motifLen = motifs.length;

        for (String motif : motifs) {
            for (int j = 0; j < motif.length(); j++) {
                switch (motif.charAt(j)) {
                    case 'A':
                        profileMatrix[A][j] += 1;
                        //System.out.println("A");
                        break;
                    case 'C':
                        profileMatrix[C][j] += 1;
                        //System.out.println("C");
                        break;
                    case 'G':
                        profileMatrix[G][j] += 1;
                        //System.out.println("G");
                        break;
                    case 'T':
                        profileMatrix[T][j] += 1;
                        //System.out.println("T");
                        break;
                }
            }
        }

        for (int i = 0; i < profileMatrix.length; i++) {
            for (int j = 0; j < profileMatrix[i].length; j++) {
                profileMatrix[i][j] /= motifLen;
            }
        }


        return profileMatrix;
    }

    private static String[] initiateMotifs(String[] sequences , int consensusStringLen) {
        SecureRandom random = new SecureRandom();
        String[] motifs = new String[InputFileGenerator.TOTAL_LINES];

        for(int i = 0; i < motifs.length; i++) {
            int random_i = random.nextInt(InputFileGenerator.LINE_LENGTH - consensusStringLen);
            motifs[i] = sequences[i].substring(random_i, random_i + consensusStringLen);
        }
        return motifs;
    }

    private static String[] readDNAFile() {
        String[] sequences = new String[InputFileGenerator.TOTAL_LINES];
        int i = 0;

        try {
            File file = new File("dna.txt");
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                sequences[i] = scanner.nextLine();
                i++;
            }
            scanner.close();
            return sequences;
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
            return new String[0];
        }
    }
}