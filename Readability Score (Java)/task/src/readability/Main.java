package readability;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Main <filename>");
            return;
        }
        String filename = args[0];
        try {
            String text = new String(Files.readAllBytes(Paths.get(filename)));
            int characters = countCharacters(text);
            int words = countWords(text);
            int sentences = countSentences(text);
            int syllables = countSyllables(text);
            int polysyllable = countPolysyllable(text);

            System.out.println("The text is: " );
            System.out.println(text);
            System.out.println("\nWords: " + words);
            System.out.println("Sentences: " + sentences);
            System.out.println("Characters: " + characters);
            System.out.println("Syllables: " + syllables);
            System.out.println("Polysyllables: " + polysyllable);

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all):");

            String choice = scanner.nextLine().trim();


            switch (choice.toUpperCase()){
                case "ARI":
                    double ARIScore = calculateARIScore(characters, words, sentences);
                    printTest(ARIScore, "Automated Readability Index");
                    break;
                case "FK":
                    double FKScore = calculateFKScore(words, sentences, syllables);
                    printTest(FKScore, "Flesch–Kincaid readability tests");
                    break;
                case "SMOG":
                    double SMOGScore = calculateSMOGScore(sentences, polysyllable);
                    printTest(SMOGScore, "Simple Measure of Gobbledygook");
                    break;
                case "CL":
                    double CLScore = calculateCLScore(characters, words, sentences);
                    printTest(CLScore, "Coleman–Liau index");
                    break;
                case "ALL":
                    printAll(characters, words, sentences, syllables, polysyllable);
                    break;
                default:
                    System.out.println("Invalid choice");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void printTest(double score, String name) {
        String ageGroup = getAgeGroup(score);
        System.out.printf("\n%s: %.2f (about %s).\n", name, score, ageGroup);
    }

    private static void printAll(int characters, int words, int sentences, int syllables, int polysyllable) {
        double ARIScore = calculateARIScore(characters, words, sentences);
        double FKScore = calculateFKScore(words, sentences, syllables);
        double SMOGScore = calculateSMOGScore(sentences, polysyllable);
        double CLScore = calculateCLScore(characters, words, sentences);

        printTest(ARIScore, "Automated Readability Index");
        printTest(FKScore, "Flesch–Kincaid readability tests");
        printTest(SMOGScore, "Simple Measure of Gobbledygook");
        printTest(CLScore, "Coleman–Liau index");

        int ariAge = getAgeUpperBound(ARIScore);
        int fkAge = getAgeUpperBound(FKScore);
        int smogAge = getAgeUpperBound(SMOGScore);
        int clAge = getAgeUpperBound(CLScore);

        double averageAge = (ariAge + fkAge + smogAge + clAge) / 4.0;
        System.out.printf("\nThis text should be understood in average by %.2f- years old.\n", averageAge);

    }

    private static int getAgeUpperBound(double score) {
        int roundedScore = (int) Math.ceil(score);
        switch (roundedScore){
            case 1: return 6;
            case 2: return 7;
            case 3: return 8;
            case 4: return 9;
            case 5: return 10;
            case 6: return 11;
            case 7: return 12;
            case 8: return 13;
            case 9: return 14;
            case 10: return 15;
            case 11: return 16;
            case 12: return 17;
            case 13: return 18;
            default: return 22;
        }
    }

    private static double calculateCLScore(int characters, int words, int sentences) {
        double L = ((double) characters / words) * 100;
        double S = ((double) sentences / words) * 100;
        return 0.0588 * L - 0.296 * S - 15.8;
    }


    private static double calculateSMOGScore(int sentences, int polysyllable) {
        return 1.043 * Math.sqrt(polysyllable * ((double) 30 / sentences)) + 3.1291;
    }


    private static double calculateFKScore(int words, int sentences, int syllables) {
        return 0.39 * ((double) words / sentences) + 11.8 * ((double) syllables / words) - 15.59;
    }


    private static double calculateARIScore(int characters, int words, int sentences) {
        return 4.71 *((double) characters / words) + 0.5 *((double) words / sentences) - 21.43;
    }

    private static int countPolysyllable(String text) {
        String[] words = text.split("\\s+");
        int polysyllables = 0;

        for (String word : words) {
            int syllables = countSyllablesInWord(word);
            if (syllables > 2){
                polysyllables++;
            }
        }
        return polysyllables;
    }

    private static int countSyllablesInWord(String word) {
        String vowels = "aeiouy";
        String w = word.toLowerCase().replaceAll("[^a-z]", "");
        if (w.isEmpty()) return 0;

        int count = 0;
        boolean prevVowel = false;
        char[] chars = w.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (vowels.indexOf(c) >= 0) {
                if (!prevVowel) count++;
                prevVowel = true;
            } else
                prevVowel = false;
        }
        if (w.endsWith("e") && count > 1) count--;
        if (count == 0) count = 1;
        return count;

    }

    private static int countSyllables(String text) {
       String[] words = text.split("\\s+");
       int totalSyllables = 0;

       for (String word : words) {
           totalSyllables += countSyllablesInWord(word);
       }
       return totalSyllables;
    }

    private static String getAgeGroup(double score) {
        int roundedScore = (int) Math.ceil(score);
        switch (roundedScore) {
            case 1: return "5-6 year-olds";
            case 2: return "6-7 year-olds";
            case 3: return "7-8 year-olds";
            case 4: return "8-9 year-olds";
            case 5: return "9-10 year-olds";
            case 6: return "10-11 year-olds";
            case 7: return "11-12 year-olds";
            case 8: return "12-13 year-olds";
            case 9: return "13-14 year-olds";
            case 10: return "14-15 year-olds";
            case 11: return "15-16 year-olds";
            case 12: return "16-17 year-olds";
            case 13: return "17-18 year-olds";
            default: return "18-22 year-olds";
        }
    }


    private static int countSentences(String text) {
        return text.split("[.!?]").length;
    }

    private static int countWords(String text) {
        return text.split("\\s+").length;
    }

    private static int countCharacters(String text) {
        return text.replaceAll("\\s", "").length();
    }
}
