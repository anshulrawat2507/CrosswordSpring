// DictionaryService.java
package com.example.demo.service;

import com.example.demo.model.Trie;
import com.example.demo.repository.DictionaryWordRepository;
import com.example.demo.repository.WordFrequencyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class DictionaryService {

    private final DictionaryWordRepository dictionaryWordRepository;
    private final WordFrequencyRepository wordFrequencyRepository;
    private Map<String, Integer> frequencyMapCache;

    public DictionaryService(DictionaryWordRepository dictionaryWordRepository,
                             WordFrequencyRepository wordFrequencyRepository) {
        this.dictionaryWordRepository = dictionaryWordRepository;
        this.wordFrequencyRepository = wordFrequencyRepository;
        this.frequencyMapCache = loadFrequencyMap();
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> loadFrequencyMap() {
        if (frequencyMapCache == null) {
            frequencyMapCache = new HashMap<>();
            wordFrequencyRepository.findAll().forEach(wf ->
                    frequencyMapCache.put(wf.getWord(), wf.getFrequencyRank()));
        }
        return frequencyMapCache;
    }

    @Transactional(readOnly = true)
    public Trie buildTrie(int wordLength, int minFrequency) {
        Trie trie = new Trie();

        dictionaryWordRepository.findByLength(wordLength).forEach(wordEntity -> {
            String word = wordEntity.getWord();
            if (minFrequency > 0) {
                Integer rank = frequencyMapCache.get(word);
                if (rank == null || rank > minFrequency) {
                    return; // Skip
                }
            }
            trie.insert(word);
        });

        return trie;
    }
}