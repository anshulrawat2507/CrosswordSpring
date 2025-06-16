package com.example.demo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.CrosswordGrid;

@Repository
public interface CrosswordGridRepository extends JpaRepository<CrosswordGrid, Long> {

    @Query(value = "SELECT * FROM crossword_grids ORDER BY RAND() LIMIT 1", nativeQuery = true)
    CrosswordGrid findRandomGrid();
}
