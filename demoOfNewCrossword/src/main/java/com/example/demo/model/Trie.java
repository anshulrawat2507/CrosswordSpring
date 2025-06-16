// Trie.java
package com.example.demo.model;

public class Trie {
    private static final int ALPHABET_SIZE = 26;
    private static final char FIRST_LETTER = 'A';

    private final Trie[] childNodes;
    private boolean isEndOfWord;

    public Trie() {
        this.childNodes = new Trie[ALPHABET_SIZE];
        this.isEndOfWord = false;
    }

    public void insert(String word) {
        Trie currentNode = this;

        for (char letter : word.toCharArray()) {
            int letterIndex = letter - FIRST_LETTER;

            if (letterIndex < 0 || letterIndex >= ALPHABET_SIZE) {
                throw new IllegalArgumentException(
                        String.format("Invalid character '%c'. Only uppercase A-Z letters are allowed.", letter)
                );
            }

            if (currentNode.childNodes[letterIndex] == null) {
                currentNode.childNodes[letterIndex] = new Trie();
            }

            currentNode = currentNode.childNodes[letterIndex];
        }
        currentNode.isEndOfWord = true;
    }

    public boolean contains(String word) {
        Trie currentNode = this;

        for (char letter : word.toCharArray()) {
            int letterIndex = letter - FIRST_LETTER;

            if (letterIndex < 0 || letterIndex >= ALPHABET_SIZE ||
                    currentNode.childNodes[letterIndex] == null) {
                return false;
            }

            currentNode = currentNode.childNodes[letterIndex];
        }

        return currentNode.isEndOfWord;
    }

    public Trie getChild(int letterIndex) {
        return childNodes[letterIndex];
    }

    public boolean hasChild(int letterIndex) {
        return childNodes[letterIndex] != null;
    }

    public boolean hasChild(char letter) {
        return childNodes[letter - FIRST_LETTER] != null;
    }

    public TrieIterator iterator() {
        return new TrieIterator(this);
    }

    public static class TrieIterator {
        private final Trie node;
        private int currentIndex;

        public TrieIterator(Trie node) {
            this.node = node;
            this.currentIndex = -1;
        }

        public boolean next() {
            while (++currentIndex < ALPHABET_SIZE) {
                if (node.childNodes[currentIndex] != null) {
                    return true;
                }
            }
            return false;
        }

        public int getIndex() {
            return currentIndex;
        }

        public char getLetter() {
            return (char) (currentIndex + FIRST_LETTER);
        }

        public Trie getNode() {
            return node.childNodes[currentIndex];
        }
    }
}