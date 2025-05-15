package com.example.demo.service;

import com.example.demo.model.CrossWordResponse;
import com.example.demo.model.Trie;
import com.example.demo.model.WordClue;
import com.example.demo.repository.WordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CrossWordGeneratorService {

    @Autowired
    private Trie trie;

    @Autowired
    private WordsRepository wordsRepo;

    @Autowired
    private CrossWordResponse response;

    public ResponseEntity<?> gridStarter(int size) {
        char[][] grid = getRecommendedGrid(size);

        List<WordClue> wordClueList = readWordCluesFromDatabase(size);
        Map<String, String> wordToClue = new HashMap<>();
        List<String> words = new ArrayList<>();
        for (WordClue wc : wordClueList) {
            String word = wc.getWord().toUpperCase();
            String clue = wc.getClue();
            words.add(word);
            wordToClue.put(word, clue);
            trie.insert(word, clue);
        }

        CrosswordWithBlacks generator = new CrosswordWithBlacks(words, grid, trie, wordToClue);
        char[][] solutionBoard = generator.generate();

        if (solutionBoard == null) {
            return new ResponseEntity<>("Could not generate crossword with given words.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        char[][] puzzleGrid = new char[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                char ch = solutionBoard[r][c];
                if (ch == 'x') puzzleGrid[r][c] = 'x';
                else if (ch == '+') puzzleGrid[r][c] = '+';
                else puzzleGrid[r][c] = '-';
            }
        }

        response.setGrid(puzzleGrid);
        response.setWords(generator.getPlacedWords());
        response.setClues(generator.getPlacedClues());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private char[][] getRecommendedGrid(int size) {
        if (size == 5) {
            return new char[][]{
                    {'+', '+', '+', '+', '+'},
                    {'+', 'x', '+', 'x', '+'}, // Row 1
                    {'+', '+', '+', '+', '+'}, // Row 2 (middle row)
                    {'+', 'x', '+', 'x', '+'}, // Row 3
                    {'+', '+', '+', '+', '+'}  // Row 4
            };
        }
        char[][] grid = new char[size][size];
        for (char[] row : grid) Arrays.fill(row, '+');
        return grid;
    }

    public List<WordClue> readWordCluesFromDatabase(int size) {
        try {
            return wordsRepo.findAll()
                    .stream()
                    .filter(wordClue -> wordClue.getWord().length() >= 3 && wordClue.getWord().length() <= size)
                    .toList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
