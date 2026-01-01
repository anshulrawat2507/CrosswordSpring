package com.crossword.repository;

import com.crossword.model.CrosswordPuzzle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrosswordPuzzleRepository extends JpaRepository<CrosswordPuzzle, Long> {
}
