package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "crossword_grids")
public class CrosswordGrid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String grid;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "min_frequency")
    private int minFrequency;

    @Column(name = "require_unique")
    private boolean requireUnique;

    @Column(name = "allow_diagonal")
    private boolean allowDiagonal;

    @Column(name = "max_solutions")
    private int maxSolutions;

    // Default constructor
    public CrosswordGrid() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGrid() {
        return grid;
    }

    public void setGrid(String grid) {
        this.grid = grid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getMinFrequency() {
        return minFrequency;
    }

    public void setMinFrequency(int minFrequency) {
        this.minFrequency = minFrequency;
    }

    public boolean isRequireUnique() {
        return requireUnique;
    }

    public void setRequireUnique(boolean requireUnique) {
        this.requireUnique = requireUnique;
    }

    public boolean isAllowDiagonal() {
        return allowDiagonal;
    }

    public void setAllowDiagonal(boolean allowDiagonal) {
        this.allowDiagonal = allowDiagonal;
    }

    public int getMaxSolutions() {
        return maxSolutions;
    }

    public void setMaxSolutions(int maxSolutions) {
        this.maxSolutions = maxSolutions;
    }
} 