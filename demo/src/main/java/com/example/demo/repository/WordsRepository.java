package com.example.demo.repository;

import com.example.demo.model.WordClues;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordsRepository extends JpaRepository<WordClues,String> {

}
