package com.example.demo.controller;

import com.example.demo.model.CrosswordGrid;
import com.example.demo.model.CrosswordRequest;
import com.example.demo.repository.CrosswordGridRepository;
import com.example.demo.service.CrosswordGeneratorService;
import com.example.demo.service.DictionaryClueService;
import com.example.demo.service.DictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crossword")
public class CrosswordController {

    private final CrosswordGeneratorService generatorService;
    private final CrosswordGridRepository gridRepository;

    @Autowired
    private DictionaryClueService clueService;

    public CrosswordController(CrosswordGeneratorService generatorService, CrosswordGridRepository gridRepository) {
        this.generatorService = generatorService;
        this.gridRepository = gridRepository;
    }

    @GetMapping("/generate")
    public ResponseEntity<?> generateCrossword(
            @RequestParam(defaultValue = "20000") int minFrequency,
            @RequestParam(defaultValue = "true") boolean requireUnique,
            @RequestParam(defaultValue = "false") boolean allowDiagonal,
            @RequestParam(defaultValue = "50") int maxSolutions
    ) {
        try {
            List<String[]> solutions = generatorService.generateCrosswords(
                    minFrequency,
                    requireUnique,
                    allowDiagonal,
                    maxSolutions
            );

            if (solutions.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            // Store ALL solutions in the database
            for (String[] gridArray : solutions) {
                CrosswordGrid grid = new CrosswordGrid();
                grid.setGrid(String.join("\n", gridArray));
                grid.setMinFrequency(minFrequency);
                grid.setRequireUnique(requireUnique);
                grid.setAllowDiagonal(allowDiagonal);
                grid.setMaxSolutions(maxSolutions);

                gridRepository.save(grid);
            }

            return ResponseEntity.ok(solutions);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error generating crossword: " + e.getMessage());
        }
    }

    @GetMapping("/random")
    public ResponseEntity<?> getRandomCrossword() {
        try {
            CrosswordGrid grid = gridRepository.findRandomGrid();
            if (grid == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(grid);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error fetching random crossword: " + e.getMessage());
        }
    }

    @GetMapping("/clue")
    public ResponseEntity<String> getClue(@RequestParam String word) {
        return ResponseEntity.ok(clueService.getClue(word));
    }
}