package com.crossword.repository;

public interface SolverStepListener {
    void onStep(char[][] grid, int wordIndex, String action);
}