package com.example.demo.model;
import java.io.*;
import java.util.*;

public class DictionaryLoader {
    private static final String CSV_DELIMITER = ",";
    private static final int WORD_CSV_COLUMN = 0;

    public static Map<String, Integer> loadWordFrequencies(String filePath) throws IOException {
        Map<String, Integer> frequencyMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            boolean isFirstLine = true;
            int currentRank = 0;

            String line;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }

                String word = line.split(CSV_DELIMITER)[WORD_CSV_COLUMN].toUpperCase();
                frequencyMap.put(word, currentRank++);
            }
        }

        return frequencyMap;
    }


    public static int loadWordsIntoTrie(String dictionaryPath,
                                        int wordLength,
                                        Trie trie,
                                        int minFrequency,
                                        Map<String, Integer> frequencyMap,
                                        Set<String> excludedWords) throws IOException {
        int loadedCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(dictionaryPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim().toUpperCase();

                // Skip words that don't meet criteria
                if (word.length() != wordLength ||
                        excludedWords.contains(word) ||
                        (minFrequency > 0 &&
                                (!frequencyMap.containsKey(word) ||
                                        frequencyMap.get(word) > minFrequency))) {
                    continue;
                }

                trie.insert(word);
                loadedCount++;
            }
        }

        return loadedCount;
    }
}