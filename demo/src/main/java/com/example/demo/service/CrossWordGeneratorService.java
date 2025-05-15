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
        // 1) Build the base grid
        char[][] base = getRecommendedGrid(size);

        // 2) Load only 3- and 5-letter words
        List<WordClue> wordClueList = wordsRepo.findAll().stream()
                .filter(wc -> {
                    int len = wc.getWord().length();
                    return len == 3 || len == 5;
                })
                .toList();

        // 3) Insert into your trie
        Map<String,String> wordToClue = new HashMap<>();
        List<String> wordList = new ArrayList<>();
        for (WordClue wc : wordClueList) {
            String word = wc.getWord().toUpperCase();
            wordList.add(word);
            wordToClue.put(word, wc.getClue());
            trie.insert(word, wc.getClue());
        }

        // 4) Generate
        CrosswordWithBlacks gen = new CrosswordWithBlacks(wordList, base, trie, wordToClue);
        char[][] solution = gen.generate();
        if (solution == null) {
            return new ResponseEntity<>("Could not generate crossword", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 5) Convert to List<String> for your JSON response
        List<String> jsonGrid = new ArrayList<>(size);
        for (int r = 0; r < size; r++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < size; c++) {
                char ch = solution[r][c];
                if (ch == 'x') {
                    sb.append('+');             // black square
                } else {
                    sb.append(ch);              // actual letter
                }
            }
            jsonGrid.add(sb.toString());
        }

        // 6) Build a fresh response object
        CrossWordResponse resp = new CrossWordResponse();
        resp.setWords(gen.getPlacedWords());
        resp.setGrid(jsonGrid);
        resp.setClues(gen.getPlacedClues());

        return ResponseEntity.ok(resp);
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
