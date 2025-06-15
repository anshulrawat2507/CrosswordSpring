package com.example.demo.repository;

import com.example.demo.model.WordClue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WordsRepository extends JpaRepository<WordClue, Long> {
    @Query("SELECT DISTINCT w.setId FROM WordClue w")
    List<Integer> findAllDistinctSetIds();

    List<WordClue> findBySetId(int setId);
}
