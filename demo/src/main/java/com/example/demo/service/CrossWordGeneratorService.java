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
import java.util.stream.Collectors;

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

        // 2) Load words with appropriate lengths for the grid size
        List<WordClue> wordClueList = wordsRepo.findAll().stream()
                .filter(wc -> {
                    int len = wc.getWord().length();
                    if (size == 5) return len == 3 || len == 5;
                    if (size == 6) return len >= 3 && len <= 6;
                    if (size == 7) return len >= 3 && len <= 7;
                    if (size == 8) return len >= 3 && len <= 8;
                    return false;
                })
                .toList();

        Map<String,String> wordToClue = new HashMap<>();
        List<String> wordList = new ArrayList<>();
        for (WordClue wc : wordClueList) {
            String word = wc.getWord().toUpperCase();
            wordList.add(word);
            wordToClue.put(word, wc.getClue());
            trie.insert(word, wc.getClue());
        }

        // MULTIPLE ATTEMPTS WITH RANDOMIZATION
        int attempts = 20; // You can increase this for better results
        char[][] bestSolution = null;
        List<String> bestPlacedWords = new ArrayList<>();
        List<String> bestPlacedClues = new ArrayList<>();
        int maxWordsPlaced = -1;

        for (int i = 0; i < attempts; i++) {
            List<String> shuffledWords = new ArrayList<>(wordList);
            Collections.shuffle(shuffledWords);

            CrosswordWithBlacks gen = new CrosswordWithBlacks(shuffledWords, base, trie, wordToClue);
            char[][] solution = gen.generate();
            if (solution != null && gen.getPlacedWords().size() > maxWordsPlaced) {
                maxWordsPlaced = gen.getPlacedWords().size();
                bestSolution = solution;
                bestPlacedWords = new ArrayList<>(gen.getPlacedWords());
                bestPlacedClues = new ArrayList<>(gen.getPlacedClues());
            }
        }

        if (bestSolution == null) {
            return new ResponseEntity<>("Could not generate crossword", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Convert to List<String> for your JSON response
        List<String> jsonGrid = new ArrayList<>(size);
        for (int r = 0; r < size; r++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < size; c++) {
                char ch = bestSolution[r][c];
                if (ch == 'x') {
                    sb.append('+');             // black square
                } else {
                    sb.append(ch);              // actual letter
                }
            }
            jsonGrid.add(sb.toString());
        }

        // Build a fresh response object
        CrossWordResponse resp = new CrossWordResponse();
        resp.setWords(bestPlacedWords);
        resp.setGrid(jsonGrid);
        resp.setClues(bestPlacedClues);

        return ResponseEntity.ok(resp);
    }

    private char[][] getRecommendedGrid(int size) {
        switch(size) {
            case 5:
                return new char[][]{
                        {'+', '+', '+', 'x', 'x'},
                        {'+', '+', '+', '+', '+'},
                        {'+', '+', '+', '+', '+'},
                        {'+', '+', '+', '+', '+'},
                        {'x', 'x', '+', '+', '+'}
                };
            case 6:
                return new char[][]{
                        {'+', '+', '+', '+', '+', '+'},
                        {'+', 'x', '+', '+', 'x', '+'},
                        {'+', '+', '+', '+', '+', '+'},
                        {'+', '+', '+', '+', '+', '+'},
                        {'+', 'x', '+', '+', 'x', '+'},
                        {'+', '+', '+', '+', '+', '+'}
                };
            case 7:
                return new char[][]{
                        {'+', '+', '+', '+', '+', '+', '+'},
                        {'+', 'x', '+', '+', '+', 'x', '+'},
                        {'+', '+', '+', '+', '+', '+', '+'},
                        {'+', '+', '+', 'x', '+', '+', '+'},
                        {'+', '+', '+', '+', '+', '+', '+'},
                        {'+', 'x', '+', '+', '+', 'x', '+'},
                        {'+', '+', '+', '+', '+', '+', '+'}
                };
            case 8:
                return new char[][]{
                        {'+', '+', '+', '+', '+', '+', '+', '+'},
                        {'+', 'x', '+', '+', '+', '+', 'x', '+'},
                        {'+', '+', '+', '+', '+', '+', '+', '+'},
                        {'+', '+', '+', 'x', 'x', '+', '+', '+'},
                        {'+', '+', '+', 'x', 'x', '+', '+', '+'},
                        {'+', '+', '+', '+', '+', '+', '+', '+'},
                        {'+', 'x', '+', '+', '+', '+', 'x', '+'},
                        {'+', '+', '+', '+', '+', '+', '+', '+'}
                };
            default:
                char[][] grid = new char[size][size];
                for (char[] row : grid) Arrays.fill(row, '+');
                return grid;
        }
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
