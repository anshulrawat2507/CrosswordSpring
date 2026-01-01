package com.crossword.service;

import com.crossword.model.CrosswordPuzzle;
import com.crossword.repository.CrosswordPuzzleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class CrosswordPuzzleService {

    @Autowired
    private CrosswordPuzzleRepository puzzleRepository;

    private static final int GRID_SIZE = 13;
    private static final int MAX_WORDS = 10;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private List<WordClue> wordList = new ArrayList<>();

    public static class WordClue {
        public String word;
        public String clue;

        public WordClue(String word, String clue) {
            this.word = word.toUpperCase();
            this.clue = clue;
        }
    }

    public static class PlacedWord {
        public String word;
        public String clue;
        public int row;
        public int col;
        public String direction; // "across" or "down"
        public int number;

        public PlacedWord(String word, String clue, int row, int col, String direction) {
            this.word = word;
            this.clue = clue;
            this.row = row;
            this.col = col;
            this.direction = direction;
        }
    }

    public void loadWords() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("words/crossword_words.json");
            if (is == null) {
                System.err.println("Could not find crossword_words.json");
                return;
            }
            JsonNode root = objectMapper.readTree(is);
            JsonNode wordsArray = root.get("words");
            
            wordList.clear();
            for (JsonNode wordNode : wordsArray) {
                String word = wordNode.get("word").asText();
                String clue = wordNode.get("clue").asText();
                if (word.length() >= 3 && word.length() <= GRID_SIZE - 2) {
                    wordList.add(new WordClue(word, clue));
                }
            }
            System.out.println("Loaded " + wordList.size() + " words from dictionary");
        } catch (Exception e) {
            System.err.println("Error loading words: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public CrosswordPuzzle generatePuzzle() {
        if (wordList.isEmpty()) {
            loadWords();
        }

        char[][] grid = new char[GRID_SIZE][GRID_SIZE];
        for (char[] row : grid) {
            Arrays.fill(row, '#'); // '#' represents black/blocked cells
        }

        List<PlacedWord> placedWords = new ArrayList<>();
        List<WordClue> shuffledWords = new ArrayList<>(wordList);
        Collections.shuffle(shuffledWords);

        // Place first word horizontally in the middle
        WordClue firstWord = findWordOfLength(shuffledWords, 5, 7);
        if (firstWord == null) return null;

        int startRow = GRID_SIZE / 2;
        int startCol = (GRID_SIZE - firstWord.word.length()) / 2;
        placeWord(grid, firstWord.word, startRow, startCol, "across");
        placedWords.add(new PlacedWord(firstWord.word, firstWord.clue, startRow, startCol, "across"));
        shuffledWords.remove(firstWord);

        // Try to place more words
        int attempts = 0;
        int maxAttempts = 500;

        while (placedWords.size() < MAX_WORDS && attempts < maxAttempts) {
            attempts++;
            
            // Pick a random placed word to intersect with
            PlacedWord existingWord = placedWords.get(new Random().nextInt(placedWords.size()));
            
            // Try to find a word that intersects
            for (int charIdx = 0; charIdx < existingWord.word.length(); charIdx++) {
                char targetChar = existingWord.word.charAt(charIdx);
                
                // Find a word containing this character
                WordClue newWordClue = findWordWithChar(shuffledWords, targetChar, placedWords);
                if (newWordClue == null) continue;

                String newWord = newWordClue.word;
                int charPosInNewWord = newWord.indexOf(targetChar);
                if (charPosInNewWord == -1) continue;

                int newRow, newCol;
                String newDirection;

                if (existingWord.direction.equals("across")) {
                    // Place new word vertically
                    newDirection = "down";
                    newRow = existingWord.row - charPosInNewWord;
                    newCol = existingWord.col + charIdx;
                } else {
                    // Place new word horizontally
                    newDirection = "across";
                    newRow = existingWord.row + charIdx;
                    newCol = existingWord.col - charPosInNewWord;
                }

                if (canPlaceWord(grid, newWord, newRow, newCol, newDirection, placedWords)) {
                    placeWord(grid, newWord, newRow, newCol, newDirection);
                    placedWords.add(new PlacedWord(newWord, newWordClue.clue, newRow, newCol, newDirection));
                    shuffledWords.remove(newWordClue);
                    break;
                }
            }
        }

        // Assign numbers to words
        assignNumbers(placedWords);

        // Convert to puzzle
        return createPuzzleFromGrid(grid, placedWords);
    }

    private WordClue findWordOfLength(List<WordClue> words, int minLen, int maxLen) {
        for (WordClue wc : words) {
            if (wc.word.length() >= minLen && wc.word.length() <= maxLen) {
                return wc;
            }
        }
        return null;
    }

    private WordClue findWordWithChar(List<WordClue> words, char c, List<PlacedWord> placed) {
        Set<String> placedWordSet = new HashSet<>();
        for (PlacedWord pw : placed) {
            placedWordSet.add(pw.word);
        }

        List<WordClue> candidates = new ArrayList<>();
        for (WordClue wc : words) {
            if (wc.word.indexOf(c) != -1 && !placedWordSet.contains(wc.word)) {
                candidates.add(wc);
            }
        }

        if (candidates.isEmpty()) return null;
        return candidates.get(new Random().nextInt(candidates.size()));
    }

    private boolean canPlaceWord(char[][] grid, String word, int row, int col, String direction, List<PlacedWord> placedWords) {
        int dr = direction.equals("down") ? 1 : 0;
        int dc = direction.equals("across") ? 1 : 0;

        // Check bounds
        if (row < 0 || col < 0) return false;
        if (direction.equals("down") && row + word.length() > GRID_SIZE) return false;
        if (direction.equals("across") && col + word.length() > GRID_SIZE) return false;

        // Check for conflicts and proper spacing
        boolean hasIntersection = false;
        
        for (int i = 0; i < word.length(); i++) {
            int r = row + i * dr;
            int c = col + i * dc;
            char currentCell = grid[r][c];
            char newChar = word.charAt(i);

            if (currentCell != '#') {
                // Cell is occupied
                if (currentCell != newChar) {
                    return false; // Conflict
                }
                hasIntersection = true;
            } else {
                // Check adjacent cells for parallel words
                if (direction.equals("across")) {
                    // Check above and below
                    if (r > 0 && grid[r - 1][c] != '#' && !isPartOfIntersection(r - 1, c, placedWords, "down")) {
                        return false;
                    }
                    if (r < GRID_SIZE - 1 && grid[r + 1][c] != '#' && !isPartOfIntersection(r + 1, c, placedWords, "down")) {
                        return false;
                    }
                } else {
                    // Check left and right
                    if (c > 0 && grid[r][c - 1] != '#' && !isPartOfIntersection(r, c - 1, placedWords, "across")) {
                        return false;
                    }
                    if (c < GRID_SIZE - 1 && grid[r][c + 1] != '#' && !isPartOfIntersection(r, c + 1, placedWords, "across")) {
                        return false;
                    }
                }
            }
        }

        // Check cells before and after the word
        int beforeR = row - dr;
        int beforeC = col - dc;
        int afterR = row + word.length() * dr;
        int afterC = col + word.length() * dc;

        if (beforeR >= 0 && beforeC >= 0 && grid[beforeR][beforeC] != '#') {
            return false;
        }
        if (afterR < GRID_SIZE && afterC < GRID_SIZE && grid[afterR][afterC] != '#') {
            return false;
        }

        return hasIntersection || placedWords.isEmpty();
    }

    private boolean isPartOfIntersection(int row, int col, List<PlacedWord> placedWords, String direction) {
        for (PlacedWord pw : placedWords) {
            if (pw.direction.equals(direction)) {
                if (direction.equals("across")) {
                    if (pw.row == row && col >= pw.col && col < pw.col + pw.word.length()) {
                        return true;
                    }
                } else {
                    if (pw.col == col && row >= pw.row && row < pw.row + pw.word.length()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void placeWord(char[][] grid, String word, int row, int col, String direction) {
        int dr = direction.equals("down") ? 1 : 0;
        int dc = direction.equals("across") ? 1 : 0;

        for (int i = 0; i < word.length(); i++) {
            grid[row + i * dr][col + i * dc] = word.charAt(i);
        }
    }

    private void assignNumbers(List<PlacedWord> placedWords) {
        // Sort by position (top to bottom, left to right)
        placedWords.sort((a, b) -> {
            if (a.row != b.row) return a.row - b.row;
            return a.col - b.col;
        });

        Map<String, Integer> positionNumbers = new HashMap<>();
        int currentNumber = 1;

        for (PlacedWord pw : placedWords) {
            String posKey = pw.row + "-" + pw.col;
            if (positionNumbers.containsKey(posKey)) {
                pw.number = positionNumbers.get(posKey);
            } else {
                pw.number = currentNumber;
                positionNumbers.put(posKey, currentNumber);
                currentNumber++;
            }
        }
    }

    private CrosswordPuzzle createPuzzleFromGrid(char[][] grid, List<PlacedWord> placedWords) {
        try {
            // Convert grid to JSON
            ArrayNode gridArray = objectMapper.createArrayNode();
            for (int r = 0; r < GRID_SIZE; r++) {
                ArrayNode rowArray = objectMapper.createArrayNode();
                for (int c = 0; c < GRID_SIZE; c++) {
                    if (grid[r][c] == '#') {
                        rowArray.add("");
                    } else {
                        rowArray.add(String.valueOf(grid[r][c]));
                    }
                }
                gridArray.add(rowArray);
            }

            // Create across clues
            ArrayNode acrossArray = objectMapper.createArrayNode();
            ArrayNode downArray = objectMapper.createArrayNode();

            for (PlacedWord pw : placedWords) {
                ObjectNode clueNode = objectMapper.createObjectNode();
                clueNode.put("number", pw.number);
                clueNode.put("word", pw.word);
                clueNode.put("clue", pw.clue);
                clueNode.put("row", pw.row);
                clueNode.put("col", pw.col);
                clueNode.put("length", pw.word.length());

                if (pw.direction.equals("across")) {
                    acrossArray.add(clueNode);
                } else {
                    downArray.add(clueNode);
                }
            }

            String gridJson = objectMapper.writeValueAsString(gridArray);
            String acrossJson = objectMapper.writeValueAsString(acrossArray);
            String downJson = objectMapper.writeValueAsString(downArray);

            return new CrosswordPuzzle(gridJson, acrossJson, downJson, GRID_SIZE);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public CrosswordPuzzle getRandomPuzzle() {
        List<CrosswordPuzzle> puzzles = puzzleRepository.findAll();
        if (puzzles.isEmpty()) {
            return null;
        }
        return puzzles.get(new Random().nextInt(puzzles.size()));
    }

    public void savePuzzle(CrosswordPuzzle puzzle) {
        puzzleRepository.save(puzzle);
    }

    public List<CrosswordPuzzle> getAllPuzzles() {
        return puzzleRepository.findAll();
    }

    public long countPuzzles() {
        return puzzleRepository.count();
    }
}
