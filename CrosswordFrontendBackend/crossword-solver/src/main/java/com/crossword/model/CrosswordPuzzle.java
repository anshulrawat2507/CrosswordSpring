package com.crossword.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "crossword_puzzles")
public class CrosswordPuzzle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String gridData; // JSON representation of the grid

    @Lob
    @Column(columnDefinition = "TEXT")
    private String acrossClues; // JSON: [{number, word, clue, row, col}]

    @Lob
    @Column(columnDefinition = "TEXT")
    private String downClues; // JSON: [{number, word, clue, row, col}]

    private int gridSize;

    public CrosswordPuzzle() {}

    public CrosswordPuzzle(String gridData, String acrossClues, String downClues, int gridSize) {
        this.gridData = gridData;
        this.gridSize = gridSize;
        this.acrossClues = acrossClues;
        this.downClues = downClues;
    }
}
