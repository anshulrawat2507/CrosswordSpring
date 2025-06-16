package com.example.demo.service;

import com.example.demo.model.Trie;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrosswordGeneratorService {
    private final DictionaryService dictionaryService;
    private static final int FIXED_GRID_SIZE = 5;

    public CrosswordGeneratorService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public List<String[]> generateCrosswords(
            int minFrequency,
            boolean requireUnique,
            boolean allowDiagonal,
            int maxSolutions) {

        Trie horizontalTrie = dictionaryService.buildTrie(FIXED_GRID_SIZE, minFrequency);
        Trie verticalTrie = horizontalTrie; // Same size for 5x5 grid

        CrosswordSolver solver = new CrosswordSolver(
                FIXED_GRID_SIZE,
                FIXED_GRID_SIZE,
                requireUnique,
                allowDiagonal,
                horizontalTrie,
                verticalTrie,
                maxSolutions
        );

        return solver.solve();
    }
}