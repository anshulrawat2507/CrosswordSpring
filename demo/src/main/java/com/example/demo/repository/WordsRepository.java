package com.example.demo.repository;

import com.example.demo.model.WordClue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordsRepository extends JpaRepository<WordClue,String> {

}
