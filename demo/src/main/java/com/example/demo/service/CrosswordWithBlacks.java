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
    private long startTime;
    private static final long TIMEOUT_MS = 15000; // 15 seconds timeout

    // To track only the words and clues actually placed in the grid
    private final List<String> placedWords = new ArrayList<>();
    private final List<String> placedClues = new ArrayList<>();

    public CrosswordWithBlacks(List<String> words, char[][] grid, Trie trie, Map<String, String> wordToClue) {
        // Sort words by length (descending) and complexity for better placement
        this.words = new ArrayList<>(words);
        this.words.sort((a, b) -> {
            if (a.length() != b.length()) {
                return b.length() - a.length(); // Longer words first
            }
            return countUncommonLetters(b) - countUncommonLetters(a);
        });

        this.N = grid.length;
        this.grid = new char[N][N];
        for (int i = 0; i < N; i++)
            this.grid[i] = Arrays.copyOf(grid[i], N);
        this.trie = trie;
        this.wordToClue = wordToClue;
    }

    // Helper method to count uncommon letters in a word
    private int countUncommonLetters(String word) {
        Set<Character> uncommonLetters = new HashSet<>(Arrays.asList('J', 'K', 'Q', 'X', 'Z'));
        int count = 0;
        for (char c : word.toCharArray()) {
            if (uncommonLetters.contains(c)) {
                count++;
            }
        }
        return count;
    }

    public char[][] generate() {
        startTime = System.currentTimeMillis();
        backtrack(0, 0);
        if (found) {
            char[][] solution = new char[N][N];
            for (int i = 0; i < N; i++) solution[i] = Arrays.copyOf(grid[i], N);
            return solution;
        }
        return null;
    }

    private void backtrack(int row, int col) {
        // Check for timeout
        if (System.currentTimeMillis() - startTime > TIMEOUT_MS) {
            return;
        }

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

        // Early validation check for better pruning
        if (!areCurrentSegmentsValid(row, col)) {
            return;
        }

        // Try to fill a horizontal slot starting at (row, col)
        if ((col == 0 || grid[row][col - 1] == 'x') && canFillSlot(row, col, true)) {
            int len = getSlotLength(row, col, true);
            List<String> candidateWords = getMatchingWords(len);
            for (String word : candidateWords) {
                if (found) return;
                if (canPlace(word, row, col, true) && !placedWords.contains(word)) {
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
            List<String> candidateWords = getMatchingWords(len);
            for (String word : candidateWords) {
                if (found) return;
                if (canPlace(word, row, col, false) && !placedWords.contains(word)) {
                    char[] backup = place(word, row, col, false);
                    placedWords.add(word);
                    placedClues.add(wordToClue.get(word));
                    backtrack(row, col + 1);
                    if (!found) { // Only backtrack if not found
                        placedWords.remove(placedWords.size() - 1);
                        placedClues.remove(placedClues.size() - 1);
                        restore(row, col, false, backup);
                    }
                }
            }
        }

        // If we can't place any word at this position, try the next position
        backtrack(row, col + 1);
    }

    // Get words of specific length to reduce search space
    private List<String> getMatchingWords(int length) {
        List<String> result = new ArrayList<>();
        for (String word : words) {
            if (word.length() == length) {
                result.add(word);
            }
        }
        return result;
    }

    // New method for early validation
    private boolean areCurrentSegmentsValid(int currentRow, int currentCol) {
        // Check completed horizontal segments in the current row
        int col = 0;
        while (col < N) {
            while (col < N && grid[currentRow][col] == 'x') col++;
            int start = col;
            while (col < N && grid[currentRow][col] != 'x') col++;
            int end = col;

            // If segment is complete (all filled with letters) and length >= 3, check if it's valid
            if (end - start >= 3 && isSegmentComplete(currentRow, start, end, true)) {
                StringBuilder word = new StringBuilder();
                for (int i = start; i < end; i++) word.append(grid[currentRow][i]);
                String w = word.toString();
                if (!isAlpha(w)) continue;
                if (!trie.search(w)) return false;
            }
        }

        // Check completed vertical segments in the current column
        int row = 0;
        while (row < N) {
            while (row < N && grid[row][currentCol] == 'x') row++;
            int start = row;
            while (row < N && grid[row][currentCol] != 'x') row++;
            int end = row;

            // If segment is complete and length >= 3, check if it's valid
            if (end - start >= 3 && isSegmentComplete(start, currentCol, end, false)) {
                StringBuilder word = new StringBuilder();
                for (int i = start; i < end; i++) word.append(grid[i][currentCol]);
                String w = word.toString();
                if (!isAlpha(w)) continue;
                if (!trie.search(w)) return false;
            }
        }

        return true;
    }

    // Check if a segment is completely filled (no '+' cells)
    private boolean isSegmentComplete(int start, int pos, int end, boolean horizontal) {
        if (horizontal) {
            for (int i = start; i < end; i++) {
                if (grid[start][i] == '+') return false;
            }
        } else {
            for (int i = start; i < end; i++) {
                if (grid[i][pos] == '+') return false;
            }
        }
        return true;
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
