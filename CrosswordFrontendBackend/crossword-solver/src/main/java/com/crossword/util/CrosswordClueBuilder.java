package com.crossword.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class CrosswordClueBuilder {

    // 🔍 Fetch a single definition from dictionary API
    public static String getClue(String word) {
        try {
            String apiURL = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word.toLowerCase();
            HttpURLConnection connection = (HttpURLConnection) new URL(apiURL).openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() != 200) return "No clue found";

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) jsonBuilder.append(line);
            reader.close();

            JSONArray arr = new JSONArray(jsonBuilder.toString());
            JSONObject first = arr.getJSONObject(0);
            JSONArray meanings = first.getJSONArray("meanings");
            JSONArray definitions = meanings.getJSONObject(0).getJSONArray("definitions");

            return definitions.getJSONObject(0).getString("definition");
        } catch (Exception e) {
            return "No clue found";
        }
    }

    // 🧠 Main method to extract horizontal & vertical words + clues
    public static Map<String, Object> extractWordsWithClues(String gridStr) {
        String[] rows = gridStr.strip().split("\n");
        int numRows = rows.length;
        int numCols = rows[0].length();

        char[][] grid = new char[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            grid[i] = rows[i].toCharArray();
        }

        List<String> gridLines = new ArrayList<>();
        Map<String, String> horizontalClues = new LinkedHashMap<>();
        Map<String, String> verticalClues = new LinkedHashMap<>();

        // Horizontal
        for (char[] row : grid) {
            String word = new String(row);
            gridLines.add(word);
            horizontalClues.put(word, getClue(word));
        }

        // Vertical
        for (int col = 0; col < numCols; col++) {
            StringBuilder sb = new StringBuilder();
            for (int row = 0; row < numRows; row++) {
                sb.append(grid[row][col]);
            }
            String word = sb.toString();
            verticalClues.put(word, getClue(word));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("grid", gridLines);
        result.put("horizontal", horizontalClues);
        result.put("vertical", verticalClues);

        return result;
    }
}
