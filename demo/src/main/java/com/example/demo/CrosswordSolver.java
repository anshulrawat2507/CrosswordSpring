package com.example.demo;

import java.util.*;

public class CrosswordSolver {
    public static boolean  insertVerticallyIfPossible (int row, int col,int index, char crossword[][], String words[]
            ,  boolean wordFill[][]){

        String word = words[index];
        int length = word.length();

        if (row + length > 7) return false;

        if (row > 0 && crossword[row-1][col] != '+') return false;

        if (row + length < 7 && crossword[row + length ][col] != '+')return false;

        int count = 0, tempRow = row;

        if (word.length() + row > 7) return false;

        for (int i = 0;   i < word.length(); i++){
            if (crossword[tempRow][col] == '+') return false;

            else if (crossword[tempRow][col] == '-'){
                count++;

            }

            else if (crossword[tempRow][col] == word.charAt(i)) count++;
            else return false;

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

    public static boolean  insertHorizontalIfPossible (int row, int col, int index,char crossword[][], String words[],  boolean wordFill[][]){

        String word = words[index];
        int length = word.length();

        if (col + length > 7) return false;

        if (col > 0 && crossword[row][col-1] !='+') return false;

        if (col + length < 7 && crossword[row][col+ length]!= '+')return false;

        int count = 0, tempCol = col;
        for (int i = 0;  i < word.length(); i++){
            if (crossword[row][tempCol] == '+') return false;

            else if (crossword[row][tempCol] == '-'){
                count++;

            }

            else if (crossword[row][tempCol] == word.charAt(i)) count++;
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

    public static void removeHorizontal (int row, int col ,int index,char crossword[][], String words[],boolean wordFill[][]){

        for (int i= 0;  i < words[index].length(); i++){
            if (wordFill[index][i]){
                crossword[row][col + i] = '-';
            }
            wordFill[index][i] = false;

        }

    }

    public static void removeVertically (int row,int col, int index, char crossword[][], String words[],boolean wordFill[][]){

        for (int i = 0;  i< words[index].length(); i++){
            if (wordFill[index][i]){
                crossword[row + i][col] = '-';

            }
            wordFill[index][i] = false;
        }
    }

    public static boolean solveCrosswordPuzzle (char crossword[][], int index, String words[], boolean wordFill[][]){

        if (index >= words.length){
            return true;
        }

        for (int i = 0; i < 7; i++){

            for (int j = 0; j < 7; j++){
                if (crossword[i][j] == '-' || crossword[i][j] == words[index].charAt(0)){

                    boolean inserted = insertHorizontalIfPossible(i,j,index,crossword,words,wordFill);

                    if (inserted){
                        if (solveCrosswordPuzzle (crossword,index+1,words,wordFill)) return true;
                    }
                    if (inserted) removeHorizontal(i,j,index,crossword,words,wordFill);

                    inserted = insertVerticallyIfPossible(i,j,index,crossword,words,wordFill);

                    if (inserted){
                        if(solveCrosswordPuzzle (crossword,index+1,words,wordFill))
                            return true;
                    }
                    if (inserted) removeVertically(i,j,index,crossword,words,wordFill);
                }
            }
        }
        return false;
    }

    public static char [][] crosswordPuzzle(char [][] crossword, StringBuffer  wordsString) {

        String tempWords = wordsString.toString();

        String words [] = tempWords.split(";");

        //Arrays.sort(words,new MyComparator());

        boolean  wordFill [][] = new boolean [words.length][];

        for (int i = 0; i < words.length; i++){
            wordFill[i] = new boolean [words[i].length()];
        }


        solveCrosswordPuzzle(crossword, 0,words,wordFill);

        return crossword;
    }
}

class MyComparator implements Comparator <String> {
    @Override
    public int compare (String first,String second){

        if (first.length() < second.length()) return 1;
        else if (first.length() > second.length()) return -1;
        else return  0;
    }
}
