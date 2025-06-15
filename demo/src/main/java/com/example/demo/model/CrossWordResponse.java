package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class CrossWordResponse {

    private char[][] grid;
    private List<String> clues;
    private char[][] solution;
    private List<ClueStart> starts;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClueStart {
        private int row;
        private int col;
        private int clueNumber;
    }
}
