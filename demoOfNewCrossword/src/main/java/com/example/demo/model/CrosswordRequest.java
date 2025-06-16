
package com.example.demo.model;

public class CrosswordRequest {
    private int minFrequency = 20000;
    private boolean requireUnique = true;
    private boolean allowDiagonal = false;
    private int maxSolutions = 50;


    public int getMinFrequency() {
        return minFrequency;
    }

    public void setMinFrequency(int minFrequency) {
        this.minFrequency = minFrequency;
    }

    public boolean isRequireUnique() {
        return requireUnique;
    }

    public void setRequireUnique(boolean requireUnique) {
        this.requireUnique = requireUnique;
    }

    public boolean isAllowDiagonal() {
        return allowDiagonal;
    }

    public void setAllowDiagonal(boolean allowDiagonal) {
        this.allowDiagonal = allowDiagonal;
    }

    public int getMaxSolutions() {
        return maxSolutions;
    }

    public void setMaxSolutions(int maxSolutions) {
        this.maxSolutions = maxSolutions;
    }
}