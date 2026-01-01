package com.crossword.service;

import com.crossword.model.CrosswordGrid;
import com.crossword.repository.CrosswordGridRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CrosswordService {

    @Autowired
    private CrosswordGridRepository gridRepository;

    public List<String> generateCrosswords() {
        // Return all grids from the database
        return gridRepository.findAll()
                .stream()
                .map(CrosswordGrid::getGrid)
                .collect(Collectors.toList());
    }

    // Return a random crossword grid from the DB
    public String getRandomGrid() {
        List<CrosswordGrid> allGrids = gridRepository.findAll();
        if (allGrids.isEmpty()) return null;

        int randomIndex = new Random().nextInt(allGrids.size());
        return allGrids.get(randomIndex).getGrid();
    }
}
