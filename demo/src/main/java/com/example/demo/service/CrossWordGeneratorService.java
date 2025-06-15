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

    // When no setId given, pick one automatically (random)
    public ResponseEntity<?> gridStarter(int size) {
        List<Integer> allSetIds = wordsRepo.findAllDistinctSetIds();
        if (allSetIds.isEmpty()) {
            return new ResponseEntity<>("No word sets found in database.", HttpStatus.NOT_FOUND);
        }
        int randomSetId = allSetIds.get(new Random().nextInt(allSetIds.size()));
        return gridStarter(size, (long) randomSetId);
    }


    // New overloaded method accepts setId to filter words by set
    public ResponseEntity<?> gridStarter(int size, Long setId) {
        char[][] solutionBoard = new char[size][size];
        placer.setSize(size);
        for (char[] row : solutionBoard) Arrays.fill(row, '+');

        List<WordClue> wordClueList = readWordCluesFromDatabase(setId);
        if (wordClueList == null || wordClueList.isEmpty()) {
            return new ResponseEntity<>("No words found for setId: " + setId, HttpStatus.NOT_FOUND);
        }

        List<WordClue> filteredPairs = new ArrayList<>();
        for (WordClue wc : wordClueList) {
            String word = wc.getWord().toUpperCase();
            if (word.length() <= size) {
                filteredPairs.add(WordClue.builder()
                        .id(wc.getId())
                        .setId(wc.getSetId())   // <-- include setId here!
                        .word(word)
                        .clue(wc.getClue())
                        .build());

            }
        }

        Collections.shuffle(filteredPairs, new Random());

        int minWords = (size == 5) ? 5 : 7;
        int maxWords = (size == 5) ? 10 : 14;
        int numWords = Math.min(filteredPairs.size(), minWords + new Random().nextInt(maxWords - minWords + 1));

        List<WordClue> selectedPairs = filteredPairs.subList(0, numWords);

        String[] words = getWords(selectedPairs);

        if (placer.placeWords(solutionBoard, words, 0)) {
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

    // Load words filtered by setId
    public List<WordClue> readWordCluesFromDatabase(Long setId) {
        List<WordClue> list = wordsRepo.findBySetId(setId.intValue());  // or cast properly
        System.out.println("Fetching words for setId: " + setId);

        if (list == null || list.isEmpty()) {
            System.out.println("No words found for setId: " + setId);
        }

        return list;
    }



    public char[][] generatePuzzleGrid(char[][] solution) {
        int size = placer.getSize();
        char[][] puzzle = new char[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                char ch = solution[row][col];
                puzzle[row][col] = (ch == '+' || ch == 'x') ? '+' : '-';
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
