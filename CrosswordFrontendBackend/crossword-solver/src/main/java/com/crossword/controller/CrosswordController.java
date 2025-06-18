package com.crossword.controller;

import com.crossword.repository.SolverStepListener;
import com.crossword.service.CrosswordSolver;
import com.crossword.util.CrosswordClueBuilder;
import com.crossword.util.CrosswordGridParser;
import com.crossword.service.CrosswordService;
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
@CrossOrigin(origins = "http://localhost:5173")
public class CrosswordController {

    private String randomFinalGrid;
    @Autowired
    private CrosswordService service;

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


}
