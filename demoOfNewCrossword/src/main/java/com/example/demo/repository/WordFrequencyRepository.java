package com.example.demo.repository;


import com.example.demo.model.WordFrequency;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WordFrequencyRepository extends JpaRepository<WordFrequency, Long> {
    Optional<WordFrequency> findByWord(String word);
}