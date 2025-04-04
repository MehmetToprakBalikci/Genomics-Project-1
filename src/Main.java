import javax.sound.midi.SysexMessage;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.SecureRandom;
import java.util.*;

public class Main {

    private static final int DeadIterationCount = 10;
    private static final int A = 0;
    private static final int C = 1;
    private static final int G = 2;
    private static final int T = 3;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Choose preferred search method: ");
        String searchMethod = scanner.nextLine();

        System.out.print("Enter consensus string length: ");
        int consensusStringLen = scanner.nextInt();

        //Generate dna
        InputFileGenerator.main(new String[0]);
        //Read generated dna
        String[] sequences = readFile("dna.txt");
        if(searchMethod.equalsIgnoreCase("r"))
            randomizedMotifSearch(sequences, consensusStringLen);

    }

    private static void GibbsSampling(String [] sequences, int consensusStringLen) {
        //Gibbs sampling algorithm

        SecureRandom random = new SecureRandom();
        float maxProb = 0;
        String [] motifs = initiateMotifs(sequences, consensusStringLen);

        // Number of Iterations
        for(int i = 0; i < 10; i++) {
            int randomNumber = random.nextInt(motifs.length);
            String deletedMotif = motifs[randomNumber];
            String maxMotif = "";
            motifs[randomNumber] = null;
            float [][] profileMatrix = createProfileMatrix(motifs);
            for(int j = 0; j<deletedMotif.length();j++){
                String currentMotif = sequences[j].substring(j, j+consensusStringLen);
                float probability = calculateSubStringProb(currentMotif, profileMatrix);
                if(maxProb<probability){
                    maxProb = probability;
                    maxMotif = currentMotif;
                }
            }

            // This part aims to calculate the score at the end of the Gibbs sampling for each iteration
            motifs[randomNumber] = maxMotif;
            profileMatrix = createProfileMatrix(motifs);
            float score = calculateMotifScores(motifs, profileMatrix);
        }
        
        

    }

    private static void randomizedMotifSearch(String[] sequences, int consensusStringLen) {
        String[] bestMotifs = new String[InputFileGenerator.TOTAL_LINES];
        int deadIterCount = 0;
        //Generate random motifs
        String[] motifs = initiateMotifs(sequences, consensusStringLen);

        bestMotifs = motifs.clone();

        System.out.println("initial motifs: " + Arrays.toString(bestMotifs));


        do {

            float[][] profileMatrix = createProfileMatrix(motifs);

            System.out.println("\n" + Arrays.deepToString(profileMatrix));

            motifs = setMotifs(sequences, profileMatrix, consensusStringLen);

            System.out.println("\n" + Arrays.toString(motifs));

            float motifsScore = calculateMotifScores(motifs, profileMatrix);
            float bestMotifsScore = calculateMotifScores(bestMotifs, profileMatrix);

            System.out.print("\nPrevious best motifs score: " + bestMotifsScore + "\n");
            System.out.print("\nFound motifs score: " + motifsScore + "\n");

            if (bestMotifsScore<motifsScore) {
                bestMotifs = motifs.clone();
                bestMotifsScore = motifsScore;
                deadIterCount = 0;
            }
            else {
                deadIterCount++;
            }

            if(deadIterCount >= DeadIterationCount) {
                System.out.print("\nLast best motifs score: " + bestMotifsScore + "\n");
                break;
            }

            System.out.print("\nNew best motifs score: " + bestMotifsScore + "\n");

        } while (true);

        String consensusString = findConsensusString(bestMotifs);
        System.out.print("\nConsensus String: " + consensusString + "\n");
    }

    private static String findConsensusString(String[] motifs) {
        StringBuilder cs = new StringBuilder();

        for( int j = 0; j < motifs[0].length(); j++) {
            //Following format:
            //A
            //C
            //G
            //T
            int[] scores = new int[4];

            for(int i = 0; i < motifs.length; i++) {
                switch (motifs[i].charAt(j)) {
                    case 'A':
                        scores[A] += 1;
                        break;
                    case 'C':
                        scores[C] += 1;
                        break;
                    case 'G':
                        scores[G] += 1;
                        break;
                    case 'T':
                        scores[T] += 1;
                        break;
                }
            }

            int maxBaseScore = 0;
            int maxBaseIndex = 0;

           for(int i = 0; i < scores.length; i++) {
               if(scores[i] > maxBaseScore) {
                   maxBaseScore = scores[i];
                   maxBaseIndex = i;
               }
           }

            switch (maxBaseIndex) {
                case A:
                    cs.append("A");
                    break;
                case C:
                    cs.append("C");
                    break;
                case G:
                    cs.append("G");
                    break;
                case T:
                    cs.append("T");
                    break;
            }
        }



        return cs.toString();
    }

    private static float calculateMotifScores(String[] motifs, float[][] profileMatrix) {
        float totalScore = 0;

        for(int i = 0; i < motifs.length; i++) {
            float score = 1;
            for(int j = 0; j < profileMatrix[A].length; j++) {
                switch (motifs[i].charAt(j)) {
                    case 'A':
                        score *= profileMatrix[A][j];
                        break;
                    case 'C':
                        score *= profileMatrix[C][j];
                        break;
                    case 'G':
                        score *= profileMatrix[G][j];
                        break;
                    case 'T':
                        score *= profileMatrix[T][j];
                        break;
                }
            }

            totalScore += score;
        }

        return totalScore;
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
        int nullCount = 0;
        for (String motif : motifs) {

            if(motif == null) {
                nullCount++;
                continue;
            }
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
                    
                    default:
                        System.out.println("Invalid character in motif: " + motif.charAt(j));
                        break;
                }
            }
        }
        motifLen-= nullCount;
        for (int i = 0; i < profileMatrix.length; i++) {
            for (int j = 0; j < profileMatrix[i].length; j++) {
                profileMatrix[i][j] += 1;
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

    private static String[] readFile(String fileName) {
        String[] sequences = new String[InputFileGenerator.TOTAL_LINES];
        int i = 0;

        try {
            File file = new File(fileName);
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