package com.crossword.model;

import jakarta.persistence.*;

@Entity
public class CrosswordGrid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob // To store the entire grid string (with \n) as a text
    private String grid;

    public CrosswordGrid() {}

    public CrosswordGrid(String grid) {
        this.grid = grid;
    }

    public Long getId() {
        return id;
    }

    public String getGrid() {
        return grid;
    }

    public void setGrid(String grid) {
        this.grid = grid;
    }
}
