package com.crossword.service;

import com.crossword.model.CrosswordGenerator;
import com.crossword.model.DictionaryLoader;
import com.crossword.model.Trie;
import com.crossword.model.CrosswordGrid;
import com.crossword.repository.CrosswordGridRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.IOException;
import java.util.*;

@Service
public class CrosswordService {

    @Autowired
    private CrosswordGridRepository gridRepository;

    public List<String> generateCrosswords() throws IOException {
        InputStream freqStream = getClass().getClassLoader().getResourceAsStream("dictionaries/ngram_freq_dict.csv");
        InputStream dictStream = getClass().getClassLoader().getResourceAsStream("dictionaries/scrabble_words");

        Objects.requireNonNull(freqStream, "Frequency file not found");
        Objects.requireNonNull(dictStream, "Dictionary file not found");

        Map<String, Integer> freqs = DictionaryLoader.loadFrequenciesFromResource(freqStream);

        Trie hTrie = new Trie();
        DictionaryLoader.loadWordsFromResource(
                dictStream,
                CrosswordGenerator.SIZE_W,
                hTrie,
                CrosswordGenerator.MIN_FREQ_W,
                freqs,
                Set.of()
        );

        Trie vTrie = hTrie;
        if (CrosswordGenerator.SIZE_W != CrosswordGenerator.SIZE_H) {
            vTrie = new Trie();
            InputStream dictStreamV = getClass().getClassLoader().getResourceAsStream("dictionaries/scrabble_words");
            Objects.requireNonNull(dictStreamV, "Dictionary file not found");

            DictionaryLoader.loadWordsFromResource(
                    dictStreamV,
                    CrosswordGenerator.SIZE_H,
                    vTrie, // ✅ Correct position for Trie
                    CrosswordGenerator.MIN_FREQ_H,
                    freqs,
                    Set.of()
            );
        }


        CrosswordGenerator solver = new CrosswordGenerator(hTrie, vTrie);
        List<String> grids = solver.solveAndReturnGrids();

        // Save to MySQL
        for (String grid : grids) {
            gridRepository.save(new CrosswordGrid(grid));
        }

        return grids;
    }

    // ✅ NEW: Method to return a random crossword grid from the DB
    public String getRandomGrid() {
        long count = gridRepository.count();
        if (count == 0) return null;

        long randomId = 1 + new Random().nextInt((int) count);
        return gridRepository.findById(randomId)
                .map(CrosswordGrid::getGrid)
                .orElse(null);
    }
}
