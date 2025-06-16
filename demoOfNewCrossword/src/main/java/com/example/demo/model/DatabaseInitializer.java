package com.example.demo.model;

import com.example.demo.repository.DictionaryWordRepository;
import com.example.demo.repository.WordFrequencyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final DictionaryWordRepository dictionaryRepo;
    private final WordFrequencyRepository frequencyRepo;
    private final ResourceLoader resourceLoader;

    // Inject file paths from application.properties
    @Value("${crossword.dictionary-path}")
    private String dictionaryPath;

    @Value("${crossword.frequency-path}")
    private String frequencyPath;

    public DatabaseInitializer(DictionaryWordRepository dictionaryRepo,
                               WordFrequencyRepository frequencyRepo,
                               ResourceLoader resourceLoader) {
        this.dictionaryRepo = dictionaryRepo;
        this.frequencyRepo = frequencyRepo;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void run(String... args) throws Exception {
        // Load Scrabble words (one word per line)
        if (dictionaryRepo.count() == 0) {
            loadDictionaryWords();
        }

        // Load word frequencies (CSV: word,count)
        if (frequencyRepo.count() == 0) {
            loadWordFrequencies();
        }
    }

    private void loadDictionaryWords() throws Exception {
        Resource resource = resourceLoader.getResource(dictionaryPath);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim().toUpperCase();
                if (!word.isEmpty()) {
                    dictionaryRepo.save(new DictionaryWord(word, word.length()));
                }
            }
            System.out.println("Loaded " + dictionaryRepo.count() + " Scrabble words from: " + dictionaryPath);
        }
    }

    private void loadWordFrequencies() throws Exception {
        Resource resource = resourceLoader.getResource(frequencyPath);
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            boolean isFirstLine = true;
            String line;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip CSV header
                }

                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String word = parts[0].trim().toUpperCase();
                    long frequency = Long.parseLong(parts[1]);
                    frequencyRepo.save(new WordFrequency(word, (int) frequency));
                }
            }
            System.out.println("Loaded " + frequencyRepo.count() + " word frequencies from: " + frequencyPath);
        }
    }
}