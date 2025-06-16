package com.example.demo.repository;



import com.example.demo.model.DictionaryWord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DictionaryWordRepository extends JpaRepository<DictionaryWord, Long> {
    List<DictionaryWord> findByLength(int length);
}