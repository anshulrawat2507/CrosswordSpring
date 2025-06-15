package crossword;

import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        final String DICTIONARY = "C:\\Users\\ACER.DESKTOP-G5EVCFN\\OneDrive\\Desktop\\CrossWordFinal\\src\\dictionaries\\scrabble_words";
        final String FREQ_FILE = "C:\\Users\\ACER.DESKTOP-G5EVCFN\\OneDrive\\Desktop\\CrossWordFinal\\src\\dictionaries\\ngram_freq_dict.csv";

        System.out.println("Loading frequency list...");
        Map<String, Integer> freqs = DictionaryLoader.loadFrequencies(FREQ_FILE);

        System.out.println("Loading horizontal words...");
        Trie hTrie = new Trie();
        DictionaryLoader.loadWords(DICTIONARY, CrosswordSolver.SIZE_W, hTrie, CrosswordSolver.MIN_FREQ_W, freqs, Set.of());

        Trie vTrie = hTrie;
        if (CrosswordSolver.SIZE_W != CrosswordSolver.SIZE_H) {
            vTrie = new Trie();
            System.out.println("Loading vertical words...");
            DictionaryLoader.loadWords(DICTIONARY, CrosswordSolver.SIZE_H, vTrie, CrosswordSolver.MIN_FREQ_H, freqs, Set.of());
        }

        CrosswordSolver solver = new CrosswordSolver(hTrie, vTrie);
        solver.solve();

        System.out.println("Done.");
    }
}
