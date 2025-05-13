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
    private WordClue wordClue;

    @Autowired
    private WordsRepository wordsRepo;

    @Autowired
    private CrosswordSolver solver;

    @Autowired
    private WordPlacer placer;

    @Autowired
    private CrossWordResponse response;

    public  ResponseEntity<?> gridStarter(int size) {

        char[][] solutionBoard = new char[size][size];
        for (char[] row : solutionBoard) Arrays.fill(row, '+');

        List<WordClue> wordClueList = readWordCluesFromDatabase();

        List<WordClue> filteredPairs = new ArrayList<>();
        for (WordClue wc : wordClueList) {
                filteredPairs.add(new WordClue(wc.getWord().toUpperCase(), wc.getClue()));
        }

        // Randomly select 8-12 WordAndClues
        int minWords = 6;
        int maxWords = 12;
        int numWords = Math.min(12, minWords + new Random().nextInt(maxWords - minWords + 1));
        // new Random().nextInt generates a random number between (0,inclusive) and (maxWords-minWords+1, exclusive) here [0,7)

        List<WordClue> selectedPairs = filteredPairs.subList(0, numWords);


        String[] words = getWords(selectedPairs);

        if (placer.placeWords(solutionBoard, words, 0)) {

            List <String> clues = new ArrayList<>();

            for (WordClue wc : selectedPairs) {
                clues.add(wc.getWord());
            }

            char [][] puzzleGrid = generatePuzzleGrid(solutionBoard);

            response.setGrid(puzzleGrid);
            response.setClues(clues);


            return new ResponseEntity<>(response,HttpStatus.OK);
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

