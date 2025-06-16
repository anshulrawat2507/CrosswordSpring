package com.crossword.model;

import java.io.*;
import java.util.*;

public class DictionaryLoader {

    public static Map<String, Integer> loadFrequenciesFromResource(InputStream resourceStream) throws IOException {
        Map<String, Integer> freqs = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resourceStream))) {
            String line;
            boolean first = true;
            int rank = 0;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                String word = line.split(",")[0].toUpperCase();
                freqs.put(word, rank++);
            }
        }
        return freqs;
    }

    public static int loadWordsFromResource(InputStream dictStream, int length, Trie trie, int minFreq, Map<String, Integer> freqs, Set<String> banned) throws IOException {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(dictStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim().toUpperCase();
                if (line.length() != length || banned.contains(line)) continue;
                if (minFreq > 0 && (!freqs.containsKey(line) || freqs.get(line) > minFreq)) continue;
                trie.add(line);
                count++;
            }
        }
        return count;
    }
}
