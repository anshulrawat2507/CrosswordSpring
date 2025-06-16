package com.example.demo.controller;

import com.example.demo.model.CrosswordRequest;
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

    @Autowired
    private DictionaryClueService clueService;

    public CrosswordController(CrosswordGeneratorService generatorService) {
        this.generatorService = generatorService;
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

            return solutions.isEmpty()
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.ok(solutions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error generating crossword: " + e.getMessage());
        }
    }

    @GetMapping("/clue")
    public ResponseEntity<String> getClue(@RequestParam String word) {
        return ResponseEntity.ok(clueService.getClue(word));
    }
}