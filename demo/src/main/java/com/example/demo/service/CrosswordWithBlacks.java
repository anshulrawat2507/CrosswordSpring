package com.example.demo.service;

import com.example.demo.model.Trie;
import java.util.*;

public class CrosswordWithBlacks {
    private final Trie trie;
    private final List<String> words;
    private final int N;
    private final char[][] grid;
    private final Map<String, String> wordToClue;
    private boolean found = false;

    // To track only the words and clues actually placed in the grid
    private final List<String> placedWords = new ArrayList<>();
    private final List<String> placedClues = new ArrayList<>();

    public CrosswordWithBlacks(List<String> words, char[][] grid, Trie trie, Map<String, String> wordToClue) {
        this.words = new ArrayList<>(words);
        this.N = grid.length;
        this.grid = new char[N][N];
        for (int i = 0; i < N; i++)
            this.grid[i] = Arrays.copyOf(grid[i], N);
        this.trie = trie;
        this.wordToClue = wordToClue;
    }

    public char[][] generate() {
        backtrack(0, 0);
        if (found) {
            char[][] solution = new char[N][N];
            for (int i = 0; i < N; i++) solution[i] = Arrays.copyOf(grid[i], N);
            return solution;
        }
        return null;
    }

    private void backtrack(int row, int col) {
        if (found) return;

        if (row == N) {
            if (areAllSegmentsValid(grid, trie)) {
                found = true;
            }
            return;
        }
        if (col == N) {
            backtrack(row + 1, 0);
            return;
        }
        if (grid[row][col] == 'x') {
            backtrack(row, col + 1);
            return;
        }
        // Try to fill a horizontal slot starting at (row, col)
        if ((col == 0 || grid[row][col - 1] == 'x') && canFillSlot(row, col, true)) {
            int len = getSlotLength(row, col, true);
            for (String word : words) {
                if (found) return;
                if (word.length() == len && canPlace(word, row, col, true) && !placedWords.contains(word)) {
                    char[] backup = place(word, row, col, true);
                    placedWords.add(word);
                    placedClues.add(wordToClue.get(word));
                    backtrack(row, col + len);
                    if (!found) { // Only backtrack if not found
                        placedWords.remove(placedWords.size() - 1);
                        placedClues.remove(placedClues.size() - 1);
                        restore(row, col, true, backup);
                    }
                }
            }
        }
        // Try to fill a vertical slot starting at (row, col)
        if ((row == 0 || grid[row - 1][col] == 'x') && canFillSlot(row, col, false)) {
            int len = getSlotLength(row, col, false);
            for (String word : words) {
                if (found) return;
                if (word.length() == len && canPlace(word, row, col, false) && !placedWords.contains(word)) {
                    char[] backup = place(word, row, col, false);
                    placedWords.add(word);
                    placedClues.add(wordToClue.get(word));
                    backtrack(row + len, col);
                    if (!found) { // Only backtrack if not found
                        placedWords.remove(placedWords.size() - 1);
                        placedClues.remove(placedClues.size() - 1);
                        restore(row, col, false, backup);
                    }
                }
            }
        }
    }

    private boolean canFillSlot(int row, int col, boolean horizontal) {
        int len = getSlotLength(row, col, horizontal);
        return len >= 3;
    }

    private int getSlotLength(int row, int col, boolean horizontal) {
        int len = 0;
        if (horizontal) {
            while (col + len < N && grid[row][col + len] != 'x') len++;
        } else {
            while (row + len < N && grid[row + len][col] != 'x') len++;
        }
        return len;
    }

    private boolean canPlace(String word, int row, int col, boolean horizontal) {
        for (int i = 0; i < word.length(); i++) {
            char c = horizontal ? grid[row][col + i] : grid[row + i][col];
            if (c != '+' && c != word.charAt(i)) return false;
        }
        return true;
    }

    private char[] place(String word, int row, int col, boolean horizontal) {
        char[] backup = new char[word.length()];
        for (int i = 0; i < word.length(); i++) {
            if (horizontal) {
                backup[i] = grid[row][col + i];
                grid[row][col + i] = word.charAt(i);
            } else {
                backup[i] = grid[row + i][col];
                grid[row + i][col] = word.charAt(i);
            }
        }
        return backup;
    }

    private void restore(int row, int col, boolean horizontal, char[] backup) {
        for (int i = 0; i < backup.length; i++) {
            if (horizontal) grid[row][col + i] = backup[i];
            else grid[row + i][col] = backup[i];
        }
    }
    private boolean isAlpha(String s) {
        for (char c : s.toCharArray()) {
            if (c < 'A' || c > 'Z') return false;
        }
        return true;
    }

    // Checks all segments in rows and columns for validity
    public boolean areAllSegmentsValid(char[][] board, Trie trie) {
        int N = board.length;
        // Check all rows
        for (int row = 0; row < N; row++) {
            int col = 0;
            while (col < N) {
                while (col < N && board[row][col] == 'x') col++;
                int start = col;
                while (col < N && board[row][col] != 'x') col++;
                int end = col;
                if (end - start >= 3) {
                    StringBuilder word = new StringBuilder();
                    for (int i = start; i < end; i++) word.append(board[row][i]);
                    String w = word.toString();
                    if (!isAlpha(w)) continue;
                    if (!trie.search(w)) return false;
                }
            }
        }
        // Check all columns
        for (int col = 0; col < N; col++) {
            int row = 0;
            while (row < N) {
                while (row < N && board[row][col] == 'x') row++;
                int start = row;
                while (row < N && board[row][col] != 'x') row++;
                int end = row;
                if (end - start >= 3) {
                    StringBuilder word = new StringBuilder();
                    for (int i = start; i < end; i++) word.append(board[i][col]);
                    String w = word.toString();
                    if (!isAlpha(w)) continue;
                    if (!trie.search(w)) return false;
                }
            }
        }
        return true;
    }

    public List<String> getPlacedWords() {
        return new ArrayList<>(placedWords);
    }

    public List<String> getPlacedClues() {
        return new ArrayList<>(placedClues);
    }
}
