package com.crossword.model;

public class Trie {
    public static final int NUM_LETTERS = 26;
    private Trie[] nodes;

    public Trie() {
        nodes = new Trie[NUM_LETTERS];
    }

    public void add(String str) {
        Trie ptr = this;
        for (char c : str.toCharArray()) {
            int ix = c - 'A';
            if (ix < 0 || ix >= NUM_LETTERS) throw new IllegalArgumentException("Invalid character: " + c);
            if (ptr.nodes[ix] == null) {
                ptr.nodes[ix] = new Trie();
            }
            ptr = ptr.nodes[ix];
        }
    }

    public boolean has(String str) {
        Trie ptr = this;
        for (char c : str.toCharArray()) {
            int ix = c - 'A';
            if (ix < 0 || ix >= NUM_LETTERS) return false;
            if (ptr.nodes[ix] == null) return false;
            ptr = ptr.nodes[ix];
        }
        return true;
    }

    public Trie descend(int ix) {
        return nodes[ix];
    }

    public boolean hasIx(int ix) {
        return nodes[ix] != null;
    }

    public boolean hasLetter(char c) {
        return nodes[c - 'A'] != null;
    }

    public Iter iter() {
        return new Iter(nodes);
    }

    public static class Iter {
        private final Trie[] nodes;
        private int ix;

        public Iter(Trie[] nodes) {
            this.nodes = nodes;
            this.ix = -1;
        }

        public boolean next() {
            while (++ix < NUM_LETTERS) {
                if (nodes[ix] != null) return true;
            }
            return false;
        }

        public int getIx() {
            return ix;
        }

        public char getLetter() {
            return (char) (ix + 'A');
        }

        public Trie get() {
            return nodes[ix];
        }
    }
}
