package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component

public class WordClue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String word;
    @Lob  // allows large text (up to 64KB for MySQL)
    @Column(length = 1000)  // optional, gives a hint
    private String clue;

}
