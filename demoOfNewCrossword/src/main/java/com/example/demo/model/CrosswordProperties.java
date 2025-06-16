package com.example.demo.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "crossword")
public class CrosswordProperties {
    private String dictionaryPath;
    private String frequencyFilePath;
    private int defaultWidth = 5;
    private int defaultHeight = 5;
    private int minWordFrequency = 20000;
    private boolean requireUniqueSolutions = true;
    private boolean allowDiagonalWords = false;
    private int maxSolutions = 50;

    public String getDictionaryPath() {
        return dictionaryPath;
    }

    public void setDictionaryPath(String dictionaryPath) {
        this.dictionaryPath = dictionaryPath;
    }

    public String getFrequencyFilePath() {
        return frequencyFilePath;
    }

    public void setFrequencyFilePath(String frequencyFilePath) {
        this.frequencyFilePath = frequencyFilePath;
    }

    public int getDefaultWidth() {
        return defaultWidth;
    }

    public void setDefaultWidth(int defaultWidth) {
        this.defaultWidth = defaultWidth;
    }

    public int getDefaultHeight() {
        return defaultHeight;
    }

    public void setDefaultHeight(int defaultHeight) {
        this.defaultHeight = defaultHeight;
    }

    public int getMinWordFrequency() {
        return minWordFrequency;
    }

    public void setMinWordFrequency(int minWordFrequency) {
        this.minWordFrequency = minWordFrequency;
    }

    public boolean isRequireUniqueSolutions() {
        return requireUniqueSolutions;
    }

    public void setRequireUniqueSolutions(boolean requireUniqueSolutions) {
        this.requireUniqueSolutions = requireUniqueSolutions;
    }

    public boolean isAllowDiagonalWords() {
        return allowDiagonalWords;
    }

    public void setAllowDiagonalWords(boolean allowDiagonalWords) {
        this.allowDiagonalWords = allowDiagonalWords;
    }

    public int getMaxSolutions() {
        return maxSolutions;
    }

    public void setMaxSolutions(int maxSolutions) {
        this.maxSolutions = maxSolutions;
    }
}