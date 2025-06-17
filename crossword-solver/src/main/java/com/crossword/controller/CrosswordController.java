package com.crossword.controller;

import com.crossword.util.CrosswordClueBuilder;
import com.crossword.util.CrosswordGridParser;
import com.crossword.service.CrosswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/crossword")
public class CrosswordController {

    @Autowired
    private CrosswordService service;

    @GetMapping("/generate")
    public List<String> generateCrossword() throws IOException {
        return service.generateCrosswords();
    }

    @GetMapping("/random")
    public String getRandomCrossword() {
        String grid = service.getRandomGrid();
        return grid != null ? grid : "No grids available in the database.";
    }

    @GetMapping("/random/details")
    public Map<String, Object> getRandomCrosswordWithWords() {
        String grid = service.getRandomGrid();
        if (grid == null) {
            return Map.of("message", "No grids available in the database.");
        }
        return CrosswordGridParser.extractWords(grid);
    }

    @GetMapping("/random/clues")
    public Map<String, Object> getRandomCrosswordWithClues() {
        String grid = service.getRandomGrid();
        if (grid == null) {
            return Map.of("message", "No grids available in the database.");
        }
        return CrosswordClueBuilder.extractWordsWithClues(grid);
    }



}
