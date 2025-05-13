package com.example.demo.service;

import com.example.demo.CrosswordSolver;
import com.example.demo.model.CrossWordResponse;
import com.example.demo.model.WordClue;
import com.example.demo.model.WordPlacer;
import com.example.demo.repository.WordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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

        // Filter by word length
        List<WordClue> filteredPairs = new ArrayList<>();
        for (WordClue wc : wordClueList) {
            String word = wc.getWord().toUpperCase();
            if (word.length() <= size) {
                filteredPairs.add(new WordClue(wc.getId(), word, wc.getClue()));
            }
        }

        // Shuffle for randomness
        Collections.shuffle(filteredPairs, new Random());

        int minWords = 0;
        int maxWords = 0;

        if (size == 5){
            minWords = 5;
            maxWords = 10;
        }

        else {
            minWords = 7;
            maxWords = 14;
        }
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

            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        return new ResponseEntity<>("Could not generate crossword with given words.", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    public List <WordClue> readWordCluesFromDatabase() {

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

        int size = placer.getSize();
        char[][] puzzle = new char[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                char ch = solution[row][col];
                if (ch == '+' || ch == 'x') puzzle[row][col] = '+';
                else puzzle[row][col] = '-';
            }
        }

        return puzzle;
    }

    public String[] getWords (List <WordClue> wordClue){

        String[] words = new String[wordClue.size()];
        int index = 0;

        for (WordClue wc : wordClue){
            words[index++] = wc.getWord();
        }
        return words;
    }

}

