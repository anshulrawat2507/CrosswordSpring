package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.io.*;

@RestController
@RequestMapping("/api")
public class GridGenerator {

    // Helper class to keep word and clue together
    static class WordClue {
        String word;
        String clue;
        WordClue(String word, String clue) {
            this.word = word;
            this.clue = clue;
        }
    }

    @RequestMapping("/GridStarter")
    public static void GridStarter(String[] args) {
        InputStream in = GridGenerator.class.getClassLoader().getResourceAsStream("words.csv");
        if (in == null) {
            System.out.println ("words.csv not found in resources!");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        int SIZE = CrosswordPlacer.SIZE;
        char[][] solutionBoard = new char[SIZE][SIZE];
        for (char[] row : solutionBoard) Arrays.fill(row, '+');

        // Path to your CSV file




        // Read word-clue pairs from CSV
        List<WordClue> wordClueList = readWordCluesFromBufferedReader(br);


        // Filter pairs by word length 3 to SIZE
        List<WordClue> filteredPairs = new ArrayList<>();
        for (WordClue wc : wordClueList) {
            if (wc.word.length() >= 3 && wc.word.length() <= SIZE) {
                filteredPairs.add(new WordClue(wc.word.toUpperCase(), wc.clue));
            }
        }

        // Randomly select 8-12 pairs
        int minWords = 8;
        int maxWords = 12;
        int numWords = Math.min(filteredPairs.size(), minWords + new Random().nextInt(maxWords - minWords + 1));
        Collections.shuffle(filteredPairs);
        List<WordClue> selectedPairs = filteredPairs.subList(0, numWords);

        // Extract words for the crossword generator
        String[] words = selectedPairs.stream().map(wc -> wc.word).toArray(String[]::new);

        if (CrosswordPlacer.placeWords(solutionBoard, words, 0)) {
            System.out.println(" Crossword Solution:\n");
            printBoard(solutionBoard);

            System.out.println("\n  Puzzle Grid (fillable = '-', blocked = '+'):\n");

            // Print clues for the words used
            System.out.println("\n Clues for this crossword:");
            StringBuffer  original = new StringBuffer();
            for (WordClue wc : selectedPairs) {
                System.out.println(wc.word + ": " + wc.clue);
                if (original.isEmpty()) original.append(wc.word);
                else original.append(";").append(wc.word);
            }
            char [][] puzzleGrid = generatePuzzleGrid(solutionBoard);

            printBoard(puzzleGrid);
            char [][] result = CrosswordSolver.crosswordPuzzle(puzzleGrid,original);

            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 7; j++){
                    System.out.print (result[i][j] + " " );
                }
                System.out.println();
            }

        } else {
            System.out.println("Could not generate crossword with given words.");
        }

    }

    // Reads word-clue pairs from CSV, skipping the header
    static List<WordClue> readWordCluesFromBufferedReader(BufferedReader br) {
        List<WordClue> pairs = new ArrayList<>();
        try {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { // skip header
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",", 2);
                if (parts.length == 2 && !parts[0].trim().isEmpty() && !parts[1].trim().isEmpty()) {
                    pairs.add(new WordClue(parts[0].trim(), parts[1].trim()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV: " + e.getMessage());
        }
        return pairs;
    }


    static char[][] generatePuzzleGrid(char[][] solution) {

        int SIZE = CrosswordPlacer.SIZE;
        char[][] puzzle = new char[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                char ch = solution[row][col];
                if (ch == '+' || ch == 'x') puzzle[row][col] = '+';
                else puzzle[row][col] = '-';
            }
        }

        return puzzle;
    }
    public static void printBoard (char [][]board){

        for (char row[] : board){
            for (char ch : row){
                System.out.print (ch + " ");
            }
            System.out.println();
        }
    }


}

