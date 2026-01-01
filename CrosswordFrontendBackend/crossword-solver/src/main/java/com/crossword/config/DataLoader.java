package com.crossword.config;

import com.crossword.model.CrosswordPuzzle;
import com.crossword.service.CrosswordPuzzleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private CrosswordPuzzleService puzzleService;

    @Override
    public void run(String... args) {
        System.out.println("🎯 Starting crossword puzzle generation...");
        
        // Load words from JSON file
        puzzleService.loadWords();
        
        // Generate multiple puzzles
        int puzzlesToGenerate = 10;
        int successfulPuzzles = 0;
        
        for (int i = 0; i < puzzlesToGenerate; i++) {
            CrosswordPuzzle puzzle = puzzleService.generatePuzzle();
            if (puzzle != null) {
                puzzleService.savePuzzle(puzzle);
                successfulPuzzles++;
            }
        }
        
        System.out.println("✅ Generated and saved " + successfulPuzzles + " crossword puzzles!");
    }
}
