package com.example.demo.model;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class WordsCsvRepresentation {

    @CsvBindByName(column = "word")
    private String word;
    @CsvBindByName(column = "clue")
    private String clue;

}
