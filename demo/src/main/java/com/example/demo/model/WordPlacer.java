package com.example.demo.model;

import org.springframework.stereotype.Component;

@Component
public class WordPlacer {

    private int size;

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    // Main method to place all words recursively
    public boolean placeWords(char[][] board, String[] words, int index) {
        if (index == words.length) {
            return true; // All words placed
        }

        String word = words[index];
        int len = word.length();

        // Alternate orientation: even index tries horizontal first, odd tries vertical first
        boolean tryHorizontalFirst = (index % 2 == 0);

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                if (tryHorizontalFirst) {
                    // Try horizontal first
                    if (canPlaceWord(board, word, row, col, true)) {
                        char[] backup = placeWordAt(board, word, row, col, true);
                        if (placeWords(board, words, index + 1)) {
                            return true;
                        }
                        // Backtrack
                        restoreBoard(board, backup, row, col, true);
                    }
                    // Then vertical
                    if (canPlaceWord(board, word, row, col, false)) {
                        char[] backup = placeWordAt(board, word, row, col, false);
                        if (placeWords(board, words, index + 1)) {
                            return true;
                        }
                        // Backtrack
                        restoreBoard(board, backup, row, col, false);
                    }
                } else {
                    // Try vertical first
                    if (canPlaceWord(board, word, row, col, false)) {
                        char[] backup = placeWordAt(board, word, row, col, false);
                        if (placeWords(board, words, index + 1)) {
                            return true;
                        }
                        // Backtrack
                        restoreBoard(board, backup, row, col, false);
                    }
                    // Then horizontal
                    if (canPlaceWord(board, word, row, col, true)) {
                        char[] backup = placeWordAt(board, word, row, col, true);
                        if (placeWords(board, words, index + 1)) {
                            return true;
                        }
                        // Backtrack
                        restoreBoard(board, backup, row, col, true);
                    }
                }
            }
        }

        return false; // Could not place this word
    }

    // Check if word can be placed at position (row, col)
    // horizontally if isHorizontal == true, else vertically
    private boolean canPlaceWord(char[][] board, String word, int row, int col, boolean isHorizontal) {
        int len = word.length();

        if (isHorizontal) {
            if (col + len > size) return false;

            for (int i = 0; i < len; i++) {
                char ch = board[row][col + i];
                if (ch != '+' && ch != word.charAt(i)) {
                    return false;
                }
            }
        } else { // vertical
            if (row + len > size) return false;

            for (int i = 0; i < len; i++) {
                char ch = board[row + i][col];
                if (ch != '+' && ch != word.charAt(i)) {
                    return false;
                }
            }
        }
        return true;
    }

    // Place word at position (row, col), return backup of overwritten chars for backtracking
    private char[] placeWordAt(char[][] board, String word, int row, int col, boolean isHorizontal) {
        int len = word.length();
        char[] backup = new char[len];

        if (isHorizontal) {
            for (int i = 0; i < len; i++) {
                backup[i] = board[row][col + i];
                board[row][col + i] = word.charAt(i);
            }
        } else {
            for (int i = 0; i < len; i++) {
                backup[i] = board[row + i][col];
                board[row + i][col] = word.charAt(i);
            }
        }
        return backup;
    }

    // Restore the board after backtracking
    private void restoreBoard(char[][] board, char[] backup, int row, int col, boolean isHorizontal) {
        int len = backup.length;
        if (isHorizontal) {
            for (int i = 0; i < len; i++) {
                board[row][col + i] = backup[i];
            }
        } else {
            for (int i = 0; i < len; i++) {
                board[row + i][col] = backup[i];
            }
        }
    }
}
