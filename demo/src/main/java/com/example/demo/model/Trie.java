package com.example.demo.model;

import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
class TrieNode {

    TrieNode[] child = new TrieNode[52];
    boolean  isEndOfWord;
    String clue;

    TrieNode (){
        for (int i = 0; i < 52; i++){
            child[i] = null;
        }
        isEndOfWord = false;
    }

    int getIndex (char ch){
        if (ch >= 'A' && ch <= 'Z') return ch - 39;
        else return ch - 97;
    }

}

@Component
public class Trie {

    TrieNode root;
    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word,String clue) {

        TrieNode node = root;
        for (int i = 0; i < word.length(); i++){

            int index = node.getIndex(word.charAt(i));
            if (node.child[index] ==  null) node.child[index] = new TrieNode();
            node = node.child[index];
        }
        node.isEndOfWord = true;
        node.clue = clue;
    }

    public boolean search(String word) {

        TrieNode node = root;

        for (int i = 0; i < word.length(); i++){
            int index = node.getIndex(word.charAt(i));
            if (node.child[index] == null) return false;
            node = node.child[index];
        }
        return node.isEndOfWord;
    }

    public boolean startsWith(String prefix) {
        TrieNode node = root;

        for (int i = 0; i < prefix.length(); i++){
            int index = node.getIndex(prefix.charAt(i));
            if (node.child[index] ==  null) return false;
            node = node.child[index];
        }

        return true;
    }
}
