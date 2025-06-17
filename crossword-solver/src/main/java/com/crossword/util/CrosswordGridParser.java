package com.crossword.util;
import java.util.*;
public class CrosswordGridParser {

    public static Map<String, Object> extractWords(String gridStr) {
        String[] rows = gridStr.strip().split("\n");
        int numRows = rows.length;
        int numCols = rows[0].length();

        char[][] grid = new char[numRows][numCols];

        // Fill grid
        for (int i = 0; i < numRows; i++) {
            grid[i] = rows[i].toCharArray();
        }

        List<String> horizontalWords = new ArrayList<>();
        List<String> verticalWords = new ArrayList<>();

        // Extract horizontal words
        for (char[] row : grid) {
            horizontalWords.add(new String(row));
        }

        // Extract vertical words
        for (int col = 0; col < numCols; col++) {
            StringBuilder sb = new StringBuilder();
            for (int row = 0; row < numRows; row++) {
                sb.append(grid[row][col]);
            }
            verticalWords.add(sb.toString());
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("grid", horizontalWords); // The actual grid (as a list of strings, row-wise)
        result.put("horizontal", horizontalWords);
        result.put("vertical", verticalWords);
        return result;
    }

}
