package com.example.demo.service;

import com.example.demo.CrosswordPlacer;
import com.example.demo.CrosswordSolver;
import com.example.demo.GridGenerator;
import com.example.demo.model.WordClue;
import com.example.demo.repository.WordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;


@Service
public class GridGeneratorService {

    @Autowired
    private WordClue wordClue;

    @Autowired
    private WordsRepository wordsRepo;

    @Autowired
    private CrosswordSolver solver;
    public  void GridStarter(String[] args) {

        int SIZE = CrosswordPlacer.SIZE;
        char[][] solutionBoard = new char[SIZE][SIZE];
        for (char[] row : solutionBoard) Arrays.fill(row, '+');

        List<WordClue> wordClueList = readWordCluesFromDatabase();

        List<WordClue> filteredPairs = new ArrayList<>();
        for (WordClue wc : wordClueList) {
            if (wc.getWord().length() >= 3 && wc.getWord().length() <= SIZE) {
                filteredPairs.add(new WordClue(wc.getWord().toUpperCase(), wc.getClue()));
            }
        }

        // Randomly select 8-12 pairs
        int minWords = 8;
        int maxWords = 12;
        int numWords = Math.min(filteredPairs.size(), minWords + new Random().nextInt(maxWords - minWords + 1));
        Collections.shuffle(filteredPairs);
        List<WordClue> selectedPairs = filteredPairs.subList(0, numWords);

        // Extract words for the crossword generator
        String[] words = selectedPairs.stream().map(wc -> wc.getWord()).toArray(String[]::new);

        if (CrosswordPlacer.placeWords(solutionBoard, words, 0)) {
            System.out.println(" Crossword Solution:\n");
            printBoard(solutionBoard);

            System.out.println("\n  Puzzle Grid (fillable = '-', blocked = '+'):\n");

            // Print clues for the words used
            System.out.println("\n Clues for this crossword:");
            StringBuffer  original = new StringBuffer();
            for (WordClue wc : selectedPairs) {
                System.out.println(wc.getWord() + ": " + wc.getClue());
                if (original.isEmpty()) original.append(wc.getWord());
                else original.append(";").append(wc.getWord());
            }
            char [][] puzzleGrid = generatePuzzleGrid(solutionBoard);

            printBoard(puzzleGrid);
            char [][] result = solver.crosswordPuzzle(puzzleGrid,original);

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


    public List <WordClue> readWordCluesFromDatabase() {
//        List<WordClue> pairs = new ArrayList<>();
//        try {
//            String line;
//            boolean firstLine = true;
//            while ((line = br.readLine()) != null) {
//                if (firstLine) { // skip header
//                    firstLine = false;
//                    continue;
//                }
//                String[] parts = line.split(",", 2);
//                if (parts.length == 2 && !parts[0].trim().isEmpty() && !parts[1].trim().isEmpty()) {
//                    pairs.add(new WordClue(parts[0].trim(), parts[1].trim()));
//                }
//            }
//        } catch (IOException e) {
//            System.err.println("Error reading CSV: " + e.getMessage());
//        }
//        return pairs;

        List <WordClue> pairs = new ArrayList<>();

        try {
            pairs = wordsRepo.findAll();
            return pairs;
        }
        catch (Exception e){
            return null;
        }
    }


    public char[][] generatePuzzleGrid(char[][] solution) {

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
    public  void printBoard (char [][]board){

        for (char[] row : board){
            for (char ch : row){
                System.out.print (ch + " ");
            }
            System.out.println();
        }
    }


}

