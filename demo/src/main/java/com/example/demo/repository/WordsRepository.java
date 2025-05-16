package com.example.demo.repository;

import com.example.demo.model.WordClue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

public interface WordsRepository extends JpaRepository<WordClue, Long> {

}
