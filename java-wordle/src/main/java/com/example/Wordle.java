package com.example;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * @author Marcus Clements, @M4rcusBC on GitHub
 * @since 2024-02-27
 */
/**
 * The Wordle class represents a simple word guessing game.
 * It prompts the user to guess a word of a specified length
 * and provides feedback on the correctness of the guess.
 * The program allows the user to change the word length
 * and provides a limited number of guesses.
 */
public class Wordle {

    private static boolean win = false;
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\033[0m";

    private static String[] previousGuesses = new String[5]; // size 5 for 5 total guesses

    /**
     * The main method is the entry point of the Java program.
     * It prompts the user to guess a word of a specified length
     * and provides feedback on the correctness of the guess.
     * The program allows the user to change the word length
     * and provides a limited number of guesses.
     *
     * @param args The command-line arguments passed to the program.
     */
    public static void main(String[] args) {

        String[] words = getWords("java-wordle/src/main/resources/dictionary5.txt");
        int wordLength = 5;

        Scanner input = new Scanner(System.in);

        /*
         * TODO: Implement word length selection
         * 
         * System.out.
         * print("Would you like to change the word length? Default is 5 (y/n): ");
         * 
         * boolean lengthCheck = false;
         * 
         * while (!lengthCheck) {
         * try {
         * char selection = input.next().charAt(0);
         * if (selection == 'y' || selection == 'Y') {
         * lengthCheck = true;
         * System.out.print("Please enter the desired word length (3-10): ");
         * try {
         * wordLength = input.nextInt();
         * while (wordLength < 3 || wordLength > 10) {
         * System.out.println("Invalid input. Please enter a number between 3 and 10.");
         * wordLength = input.nextInt();
         * }
         * } catch (Exception e) {
         * System.out.println("Invalid input. Please enter a number between 3 and 10.");
         * }
         * wordLength = input.nextInt();
         * } else if (selection == 'n' || selection == 'N') {
         * lengthCheck = true;
         * break;
         * } else {
         * System.out.println("Invalid input. Please enter 'y' or 'n'.");
         * }
         * } catch (Exception e) {
         * System.out.println("Invalid input. Please enter 'y' or 'n'.");
         * }
         * }
         * 
         */

        // Select a random word from the dictionary
        String selectedWord = words[(int) (Math.random() * (words.length - 1)) + 1];

        int guessCount = 0;
        int guessesLeft = 5;
        String guess = "";

        buildOutput(guessesLeft, wordLength);
        while (guessCount < 5 && !win) {

            System.out.printf("Enter guess [%d left]: ", guessesLeft);
            guess = input.nextLine();
            while (!isInDictionary(guess, words)) {
                System.err.println(
                        "Invalid guess. Please enter a " + wordLength + "-letter word that's in the dictionary.");
                System.out.printf("Enter guess [%d left]: ", guessesLeft);
                guess = input.nextLine();
            }
            previousGuesses[guessCount] = processGuess(guess, selectedWord, wordLength);
            guessCount++;
            guessesLeft--;
            buildOutput(guessesLeft, wordLength);
        }

        if (win) {
            buildOutput(guessesLeft, wordLength);
            System.out.printf("Congratulations! You guessed the word using %s%d %s!\n",
                    (guessCount < 4) ? "only " : "", guessCount, (guessCount > 1) ? "guesses" : "guess");
            // System.out.println("Would you like to play again? (y/n): ");
        } else {
            buildOutput(guessesLeft, wordLength);
            System.out.println(
                    "Sorry, you didn't guess the word within five guesses. The word was " + selectedWord + ".");
            // System.out.println("Would you like to play again? (y/n): ");
        }

        input.close();

    }

    /**
     * Prints the output of the Wordle game, including the game title, previous
     * guesses, and remaining guesses.
     * 
     * @param guessesLeft The number of remaining guesses.
     * @param wordLength  The length of the word to be guessed.
     */
    static void buildOutput(int guessesLeft, int wordLength) {

        System.out.print("\033[H\033[2J");
        System.out.flush();
        if (guessesLeft == 5) {
            System.out.println("Welcome to my Homemade Wordle Game!");
            System.out.println("I have a word in mind, and you have to guess it. You have five tries. Good luck!");
        }
        System.out.println(" ________                __ __        ");
        System.out.println("|  |  |  |.-----.----.--|  |  |.-----.");
        System.out.println("|  |  |  ||  _  |   _|  _  |  ||  -__|");
        System.out.println("|________||_____|__| |_____|__||_____|");
        System.out.println();

        for (int i = 0; i < previousGuesses.length; i++) {
            if (previousGuesses[i] != null) {
                System.out.print("      ");
                for (int j = 0; j < wordLength; j++) {
                    System.out.print("┌───┐");
                }
                System.out.println();
                System.out.print("      ");
                System.out.println(previousGuesses[i]);
                System.out.print("      ");
                for (int j = 0; j < wordLength; j++) {
                    System.out.print("└───┘");
                }
                System.out.println();
            }
        }
        System.out.println("--------------------------------------\n");
    }

    /**
     * This method reads a specified dictionary file and returns an array of
     * possible words to be used for the game.
     * 
     * @param filepath the path to the file containing the dictionary of words
     * @return String[] containing the dictionary of words to be used for the game -
     *         returns empty String array if exception is thrown
     * @throws FileNotFoundException if the file is not found
     */
    static String[] getWords(String filepath) {

        try (BufferedReader bufferedReader = new BufferedReader(
                new FileReader(filepath))) {
            return bufferedReader.lines().toArray(String[]::new);
        } catch (FileNotFoundException e) {
            System.err.println("Dictionary file not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Failed to read the dictionary file!");
            e.printStackTrace();
        }
        return new String[0];
    }

    /**
     * Checks if a given word is present in the dictionary.
     *
     * @param word       the word to be checked
     * @param dictionary the array of words in the dictionary
     * @return true if the word is present in the dictionary, false otherwise
     */
    static boolean isInDictionary(String word, String[] dictionary) {
        for (String dictWord : dictionary) {
            if (dictWord.equalsIgnoreCase(word)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Processes the guess and compares it with the word to generate a result
     * string.
     * The result string contains colored characters representing correct and
     * incorrect guesses.
     * 
     * @param guess      The user's guess.
     * @param word       The word to be guessed.
     * @param wordLength The length of the word.
     * @return The result string with colored characters representing correct and
     *         incorrect guesses.
     */
    static String processGuess(String guess, String word, int wordLength) {
        String result = "";
        int[] wordFrequency = new int[26]; // Assuming only lowercase English letters are used

        // Count the frequency of each character in the word
        for (char c : word.toCharArray()) {
            wordFrequency[Character.toLowerCase(c) - 'a']++;
        }

        for (int i = 0; i < wordLength; i++) {
            char guessChar = guess.charAt(i);
            int index = Character.toLowerCase(guessChar) - 'a';

            if (wordFrequency[index] > 0) {
                if (guessChar == word.charAt(i)) {
                    result += "| " + ANSI_GREEN + Character.toUpperCase(guessChar) + ANSI_RESET + " |";
                } else {
                    result += "| " + ANSI_YELLOW + Character.toUpperCase(guessChar) + ANSI_RESET + " |";
                }
                wordFrequency[index]--;
            } else {
                result += "| " + ANSI_RED + Character.toUpperCase(guessChar) + ANSI_RESET + " |";
            }
        }

        if (guess.equalsIgnoreCase(word)) {
            win = true;
        }
        return result;
    }
}