package com.example.demo.service;

import com.example.demo.model.CrossWordResponse;
import com.example.demo.model.WordClue;
import com.example.demo.model.WordPlacer;
import com.example.demo.repository.WordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CrossWordGeneratorService {

    @Autowired
    private WordsRepository wordsRepo;

    @Autowired
    private WordPlacer placer;

    @Autowired
    private CrossWordResponse response;

    public ResponseEntity<?> gridStarter(int size) {
        char[][] solutionBoard = new char[size][size];
        placer.setSize(size);

        for (char[] row : solutionBoard) Arrays.fill(row, '+');

        List<WordClue> wordClueList = readWordCluesFromDatabase();

        List<WordClue> filteredPairs = new ArrayList<>();
        for (WordClue wc : wordClueList) {
            String word = wc.getWord().toUpperCase();
            if (word.length() <= size) {
                filteredPairs.add(new WordClue(wc.getId(), word, wc.getClue()));
            }
        }

        Collections.shuffle(filteredPairs, new Random());

        int minWords = 0;
        int maxWords = 0;

        if (size == 5) {
            minWords = 5;
            maxWords = 10;
        } else {
            minWords = 7;
            maxWords = 14;
        }

        int numWords = Math.min(filteredPairs.size(), minWords + new Random().nextInt(maxWords - minWords + 1));
        List<WordClue> selectedPairs = filteredPairs.subList(0, numWords);
        String[] words = getWords(selectedPairs);

        if (placer.placeWords(solutionBoard, words, 0)) {

            // Clean up non-letter cells by keeping only valid letters or '+'
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    char ch = solutionBoard[i][j];
                    if (!Character.isLetter(ch)) {
                        solutionBoard[i][j] = '+';
                    }
                }
            }

            // Replace 'x' or 'X' with '+'
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (solutionBoard[i][j] == 'x' || solutionBoard[i][j] == 'X') {
                        solutionBoard[i][j] = '+';
                    }
                }
            }

            List<String> clues = new ArrayList<>();
            for (WordClue wc : selectedPairs) {
                clues.add(wc.getClue());
            }

            char[][] puzzleGrid = generatePuzzleGrid(solutionBoard);

            response.setGrid(puzzleGrid);
            response.setClues(clues);
            response.setSolution(solutionBoard);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        return new ResponseEntity<>("Could not generate crossword with given words.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public List<WordClue> readWordCluesFromDatabase() {
        try {
            return wordsRepo.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    public char[][] generatePuzzleGrid(char[][] solution) {
        int size = placer.getSize();
        char[][] puzzle = new char[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                char ch = solution[row][col];
                if (Character.isLetter(ch)) {
                    puzzle[row][col] = '-'; // hide letters
                } else {
                    puzzle[row][col] = '+'; // block or empty
                }
            }
        }
        return puzzle;
    }

    public String[] getWords(List<WordClue> wordClue) {
        String[] words = new String[wordClue.size()];
        int index = 0;
        for (WordClue wc : wordClue) {
            words[index++] = wc.getWord();
        }
        return words;
    }
}
