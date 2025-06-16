package com.crossword.controller;

import com.crossword.service.CrosswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

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
}
