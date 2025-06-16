// CrosswordSolver.java
package com.example.demo.service;

import com.example.demo.model.Trie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrosswordSolver {
    private final int gridWidth;
    private final int gridHeight;
    private final boolean requireUniqueSolutions;
    private final boolean allowDiagonalWords;
    private final int maxSolutions;

    private final char[] grid;
    private final Trie horizontalWordTrie;
    private final Trie verticalWordTrie;
    private final List<String[]> solutions = new ArrayList<>();
    private boolean solutionLimitReached = false;

    public CrosswordSolver(int gridWidth, int gridHeight, boolean requireUniqueSolutions,
                           boolean allowDiagonalWords, Trie horizontalWordTrie,
                           Trie verticalWordTrie, int maxSolutions) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.requireUniqueSolutions = requireUniqueSolutions;
        this.allowDiagonalWords = allowDiagonalWords;
        this.horizontalWordTrie = horizontalWordTrie;
        this.verticalWordTrie = verticalWordTrie;
        this.maxSolutions = maxSolutions;
        this.grid = new char[gridHeight * gridWidth];
        Arrays.fill(grid, ' ');
    }

    public List<String[]> solve() {
        Trie[] verticalTries = new Trie[gridWidth];
        Arrays.fill(verticalTries, verticalWordTrie);
        searchGrid(horizontalWordTrie, verticalTries, 0);
        return solutions;
    }

    private void searchGrid(Trie currentTrie, Trie[] verticalTries, int currentPosition) {
        if (solutionLimitReached) return;

        int row = currentPosition / gridWidth;
        int col = currentPosition % gridWidth;

        // Check if we've filled the entire grid
        if (currentPosition == gridHeight * gridWidth) {
            recordSolution();
            return;
        }

        // Reset to horizontal trie at start of each row
        if (col == 0) {
            currentTrie = horizontalWordTrie;
        }

        Trie.TrieIterator iterator = currentTrie.iterator();
        while (iterator.next()) {
            if (!verticalTries[col].hasChild(iterator.getIndex())) {
                continue;
            }

            // Try current letter
            grid[currentPosition] = iterator.getLetter();

            // Save current vertical trie state
            Trie previousVerticalTrie = verticalTries[col];
            verticalTries[col] = verticalTries[col].getChild(iterator.getIndex());

            // Recursively search next position
            searchGrid(iterator.getNode(), verticalTries, currentPosition + 1);

            // Restore vertical trie state
            verticalTries[col] = previousVerticalTrie;

            if (solutionLimitReached) return;
        }
    }

    private void recordSolution() {
        // Skip symmetric solutions if required
        if (requireUniqueSolutions && gridWidth == gridHeight && isSymmetricGrid()) {
            return;
        }

        // Convert grid to string array
        String[] gridSolution = new String[gridHeight];
        for (int row = 0; row < gridHeight; row++) {
            char[] rowChars = new char[gridWidth];
            System.arraycopy(grid, row * gridWidth, rowChars, 0, gridWidth);
            gridSolution[row] = new String(rowChars);
        }

        solutions.add(gridSolution);

        // Check solution limit
        if (solutions.size() >= maxSolutions) {
            solutionLimitReached = true;
        }
    }

    private boolean isSymmetricGrid() {
        for (int i = 0; i < gridHeight; i++) {
            for (int j = i + 1; j < gridWidth; j++) {
                if (grid[i * gridWidth + j] != grid[j * gridWidth + i]) {
                    return false;
                }
            }
        }
        return true;
    }
}