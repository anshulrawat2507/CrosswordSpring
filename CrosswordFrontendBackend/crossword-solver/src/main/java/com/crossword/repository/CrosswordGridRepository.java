package com.crossword.repository;

import com.crossword.model.CrosswordGrid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CrosswordGridRepository extends JpaRepository<CrosswordGrid, Long> {
    // Custom query to get a random grid (works for MySQL)
    @Query(value = "SELECT * FROM crossword_grid ORDER BY RAND() LIMIT 1", nativeQuery = true)
    CrosswordGrid findRandomGrid();
}
