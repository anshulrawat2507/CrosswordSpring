package com.crossword.service;

import com.crossword.repository.SolverStepListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class CrosswordSolver {

    private int size;

    public boolean  insertVerticallyIfPossible (int row, int col, int index, char[][] crossword, String[] words
            , boolean[][] wordFill){

        String word = words[index];
        int length = word.length();

        if (row + length > size) return false;

        if (row > 0 && crossword[row-1][col] != '+') return false;

        if (row + length < size && crossword[row + length ][col] != '+')return false;

        int count = 0, tempRow = row;


        for (int i = 0;   i < word.length(); i++){

            if (crossword[tempRow][col] == '+'){
                return false;
            }
            else if (crossword[tempRow][col] == '-'){
                count++;
            }
            else if (crossword[tempRow][col] == word.charAt(i)){
                count++;
            }
            else{
                return false;
            }
            tempRow++;

        }

        tempRow = row;

        if (count == word.length()){

            for (int i = 0; i < word.length(); i++){

                if (crossword[tempRow][col] != word.charAt(i)){
                    crossword[tempRow][col] = word.charAt(i);
                    wordFill[index][i] = true;
                }
                tempRow++;

            }
            return true;
        }


        return false;
    }

    public  boolean  insertHorizontalIfPossible (int row, int col, int index, char[][] crossword, String[] words, boolean[][] wordFill){

        String word = words[index];
        int length = word.length();

        if (col + length > size) return false;

        if (col > 0 && crossword[row][col-1] !='+') return false;

        if (col + length < size && crossword[row][col+ length]!= '+')return false;

        int count = 0, tempCol = col;

        for (int i = 0;  i < word.length(); i++){

            if (crossword[row][tempCol] == '+'){
                return false;
            }
            else if (crossword[row][tempCol] == '-'){
                count++;
            }
            else if (crossword[row][tempCol] == word.charAt(i)){
                count++;
            }
            else return false;

            tempCol++;
        }

        tempCol = col;

        if (count == word.length()){

            for (int i = 0; i < word.length(); i++){
                if (crossword[row][tempCol] != word.charAt(i)){
                    crossword[row][tempCol] = word.charAt(i);
                    wordFill[index][i] = true;
                }
                tempCol++;
            }

            return true;
        }

        return false;
    }

    public  void removeHorizontal (int row, int col , int index, char[][] crossword, String[] words, boolean[][] wordFill){

        for (int i= 0;  i < words[index].length(); i++){

            if (wordFill[index][i]){
                crossword[row][col + i] = '-';
            }
            wordFill[index][i] = false;

        }

    }

    public  void removeVertically (int row, int col, int index, char[][] crossword, String[] words, boolean[][] wordFill){

        for (int i = 0;  i< words[index].length(); i++){

            if (wordFill[index][i]){
                crossword[row + i][col] = '-';
            }
            wordFill[index][i] = false;
        }
    }
    public char[][] crosswordPuzzle(char[][] crossword, String[] words, SolverStepListener listener) {
        boolean[][] wordFill = new boolean[words.length][];
        for (int i = 0; i < words.length; i++) {
            wordFill[i] = new boolean[words[i].length()];
        }

        solveCrosswordPuzzle(crossword, 0, words, wordFill, listener);
        return crossword;
    }

    private boolean solveCrosswordPuzzle(char[][] crossword, int index, String[] words,
                                         boolean[][] wordFill, SolverStepListener listener) {
        if (index >= words.length) {
            if (listener != null) listener.onStep(crossword, index, "solved");
            return true;
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (crossword[i][j] == '-' || crossword[i][j] == words[index].charAt(0)) {

                    // Try horizontal placement
                    boolean inserted = insertHorizontalIfPossible(i, j, index, crossword, words, wordFill);
                    if (inserted) {
                        if (listener != null) listener.onStep(crossword, index, "placed");

                        if (solveCrosswordPuzzle(crossword, index+1, words, wordFill, listener))
                            return true;


                        removeHorizontal(i, j, index, crossword, words, wordFill);
                        if (listener != null) listener.onStep(crossword, index, "removed");
                    }

                    // Try vertical placement
                    inserted = insertVerticallyIfPossible(i, j, index, crossword, words, wordFill);
                    if (inserted) {
                        if (listener != null) listener.onStep(crossword, index, "placed");

                        if (solveCrosswordPuzzle(crossword, index+1, words, wordFill, listener))
                            return true;

                        removeVertically(i, j, index, crossword, words, wordFill);
                        if (listener != null) listener.onStep(crossword, index, "removed");
                    }
                }
            }
        }
        return false;
    }
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
