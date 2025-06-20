package com.crossword.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class CrosswordGrid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Lob
    private String grid;

    public CrosswordGrid() {}

    public CrosswordGrid(String grid) {
        this.grid = grid;
    }

}
