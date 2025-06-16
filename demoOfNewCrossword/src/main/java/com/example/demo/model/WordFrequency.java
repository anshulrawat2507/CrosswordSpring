// WordFrequency.java
package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "word_frequency")
public class WordFrequency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String word;

    @Column(name = "frequency_rank", nullable = false)
    private int frequencyRank;

    public WordFrequency() {
    }

    public WordFrequency(String word, int frequencyRank) {
        this.word = word;
        this.frequencyRank = frequencyRank;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getFrequencyRank() {
        return frequencyRank;
    }

    public void setFrequencyRank(int frequencyRank) {
        this.frequencyRank = frequencyRank;
    }
}