package com.crossword.controller;

import com.crossword.model.CrosswordPuzzle;
import com.crossword.repository.SolverStepListener;
import com.crossword.service.CrosswordSolver;
import com.crossword.service.CrosswordPuzzleService;
import com.crossword.util.CrosswordClueBuilder;
import com.crossword.util.CrosswordGridParser;
import com.crossword.service.CrosswordService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/crossword")
@CrossOrigin(origins = "*")
public class CrosswordController {

    private String randomFinalGrid;
    
    @Autowired
    private CrosswordService service;
    
    @Autowired
    private CrosswordPuzzleService puzzleService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/generate")
    public List<String> generateCrossword() throws IOException {
        return service.generateCrosswords();
    }

    @GetMapping("/random")
    public String getRandomCrossword() {
        String grid = randomFinalGrid;
        return grid != null ? grid : "No grids available in the database.";
    }

    @GetMapping("/random/details")
    public Map<String, Object> getRandomCrosswordWithWords() {
        String grid = randomFinalGrid;
        if (grid == null) {
            return Map.of("message", "No grids available in the database.");
        }
        return CrosswordGridParser.extractWords(grid);
    }

    @GetMapping("/random/clues")
    public Map<String, Object> getRandomCrosswordWithClues() {
        String grid = service.getRandomGrid();
        randomFinalGrid = grid;
        if (grid == null) {
            return Map.of("message", "No grids available in the database.");
        }
        return CrosswordClueBuilder.extractWordsWithClues(grid);
    }

    // Add this method to your existing CrosswordController
    // Add this method to your existing CrosswordController
    @GetMapping(path = "/solve/steps", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter solveWithSteps() {
        // 1) Get random crossword details
        Map<String, Object> details = getRandomCrosswordWithWords();

        List<String> horizontalWords = (List<String>) details.get("horizontal");
        List<String> verticalWords = (List<String>) details.get("vertical");


        // 2) Combine and shuffle words
        List<String> allWords = new ArrayList<>();
        allWords.addAll(horizontalWords);
        allWords.addAll(verticalWords);
        Collections.shuffle(allWords);

        // 3) Create initial empty grid (5x5)
        char[][] initialGrid = new char[5][5];
        for (char[] row : initialGrid) {
            Arrays.fill(row, '-');
        }

        // 4) Set up SSE emitter
        SseEmitter emitter = new SseEmitter(100_000L);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                CrosswordSolver solver = new CrosswordSolver();
                solver.setSize(5);

                // 5) Listener for solver updates
                SolverStepListener listener = (grid, stepIndex, action) -> {
                    try {
                        // Convert grid to List<String>
                        List<String> gridState = new ArrayList<>();
                        for (char[] row : grid) {
                            gridState.add(new String(row));
                        }

                        // Prepare event data
                        Map<String, Object> event = new HashMap<>();
                        event.put("grid", gridState);
                        event.put("step", stepIndex);
                        event.put("action", action);

                        // Send SSE event
                        emitter.send(SseEmitter.event()
                                .name("solver-update")
                                .data(event));

                        // Add small delay between steps for better visualization
                        Thread.sleep(50);

                        if ("solved".equals(action)) {
                            emitter.complete();
                        }
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                };

                // 6) Start solving
                solver.crosswordPuzzle(initialGrid, allWords.toArray(new String[0]), listener);
            } catch (Exception e) {
                emitter.completeWithError(e);
            } finally {
                executor.shutdown();
            }
        });

        return emitter;
    }

    // ==================== NEW PROPER CROSSWORD ENDPOINTS ====================

    @GetMapping("/puzzle")
    public Map<String, Object> getRandomPuzzle() {
        CrosswordPuzzle puzzle = puzzleService.getRandomPuzzle();
        
        if (puzzle == null) {
            return Map.of("error", "No puzzles available");
        }

        try {
            Map<String, Object> result = new LinkedHashMap<>();
            
            // Parse grid
            JsonNode gridNode = objectMapper.readTree(puzzle.getGridData());
            List<List<String>> grid = new ArrayList<>();
            for (JsonNode row : gridNode) {
                List<String> rowList = new ArrayList<>();
                for (JsonNode cell : row) {
                    rowList.add(cell.asText());
                }
                grid.add(rowList);
            }
            result.put("grid", grid);
            result.put("gridSize", puzzle.getGridSize());

            // Parse across clues
            JsonNode acrossNode = objectMapper.readTree(puzzle.getAcrossClues());
            List<Map<String, Object>> acrossClues = new ArrayList<>();
            for (JsonNode clue : acrossNode) {
                Map<String, Object> clueMap = new LinkedHashMap<>();
                clueMap.put("number", clue.get("number").asInt());
                clueMap.put("clue", clue.get("clue").asText());
                clueMap.put("answer", clue.get("word").asText());
                clueMap.put("row", clue.get("row").asInt());
                clueMap.put("col", clue.get("col").asInt());
                clueMap.put("length", clue.get("length").asInt());
                acrossClues.add(clueMap);
            }
            result.put("across", acrossClues);

            // Parse down clues
            JsonNode downNode = objectMapper.readTree(puzzle.getDownClues());
            List<Map<String, Object>> downClues = new ArrayList<>();
            for (JsonNode clue : downNode) {
                Map<String, Object> clueMap = new LinkedHashMap<>();
                clueMap.put("number", clue.get("number").asInt());
                clueMap.put("clue", clue.get("clue").asText());
                clueMap.put("answer", clue.get("word").asText());
                clueMap.put("row", clue.get("row").asInt());
                clueMap.put("col", clue.get("col").asInt());
                clueMap.put("length", clue.get("length").asInt());
                downClues.add(clueMap);
            }
            result.put("down", downClues);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "Failed to parse puzzle: " + e.getMessage());
        }
    }

    @GetMapping("/puzzle/new")
    public Map<String, Object> generateNewPuzzle() {
        CrosswordPuzzle puzzle = puzzleService.generatePuzzle();
        
        if (puzzle == null) {
            return Map.of("error", "Failed to generate puzzle");
        }
        
        puzzleService.savePuzzle(puzzle);
        
        // Return the newly generated puzzle
        return getPuzzleAsMap(puzzle);
    }

    @GetMapping("/puzzle/count")
    public Map<String, Object> getPuzzleCount() {
        return Map.of("count", puzzleService.countPuzzles());
    }

    private Map<String, Object> getPuzzleAsMap(CrosswordPuzzle puzzle) {
        try {
            Map<String, Object> result = new LinkedHashMap<>();
            
            JsonNode gridNode = objectMapper.readTree(puzzle.getGridData());
            List<List<String>> grid = new ArrayList<>();
            for (JsonNode row : gridNode) {
                List<String> rowList = new ArrayList<>();
                for (JsonNode cell : row) {
                    rowList.add(cell.asText());
                }
                grid.add(rowList);
            }
            result.put("grid", grid);
            result.put("gridSize", puzzle.getGridSize());

            JsonNode acrossNode = objectMapper.readTree(puzzle.getAcrossClues());
            List<Map<String, Object>> acrossClues = new ArrayList<>();
            for (JsonNode clue : acrossNode) {
                Map<String, Object> clueMap = new LinkedHashMap<>();
                clueMap.put("number", clue.get("number").asInt());
                clueMap.put("clue", clue.get("clue").asText());
                clueMap.put("answer", clue.get("word").asText());
                clueMap.put("row", clue.get("row").asInt());
                clueMap.put("col", clue.get("col").asInt());
                clueMap.put("length", clue.get("length").asInt());
                acrossClues.add(clueMap);
            }
            result.put("across", acrossClues);

            JsonNode downNode = objectMapper.readTree(puzzle.getDownClues());
            List<Map<String, Object>> downClues = new ArrayList<>();
            for (JsonNode clue : downNode) {
                Map<String, Object> clueMap = new LinkedHashMap<>();
                clueMap.put("number", clue.get("number").asInt());
                clueMap.put("clue", clue.get("clue").asText());
                clueMap.put("answer", clue.get("word").asText());
                clueMap.put("row", clue.get("row").asInt());
                clueMap.put("col", clue.get("col").asInt());
                clueMap.put("length", clue.get("length").asInt());
                downClues.add(clueMap);
            }
            result.put("down", downClues);

            return result;
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }
}
