// DictionaryWord.java
package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dictionary_word")
public class DictionaryWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String word;

    @Column(nullable = false)
    private int length;

    public DictionaryWord() {
    }

    public DictionaryWord(String word, int length) {
        this.word = word;
        this.length = length;
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

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}