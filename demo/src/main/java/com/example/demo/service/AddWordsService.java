package com.example.demo.service;

import com.example.demo.model.WordClues;
import com.example.demo.model.WordsCsvRepresentation;
import com.example.demo.repository.WordsRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.apache.tomcat.jni.Buffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.ResultSetWrappingSqlRowSetMetaData;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AddWordsService {

    @Autowired
    private WordsRepository wordsRepo;

    public String addWordsToDatabase(MultipartFile file) throws IOException {

        Set <WordClues> wordClues = parseCsv(file);

        wordsRepo.saveAll(wordClues);
        return "Database update successful: Words stored.";
    }

    public Set<WordClues> parseCsv (MultipartFile file) throws IOException {

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))){

            HeaderColumnNameMappingStrategy< WordsCsvRepresentation> strategy =new HeaderColumnNameMappingStrategy<>();

            strategy.setType(WordsCsvRepresentation.class);

            CsvToBean<WordsCsvRepresentation> csvToBean =new CsvToBeanBuilder<WordsCsvRepresentation>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreEmptyLine(true)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse()
                    .stream()
                    .map ( csv -> WordClues.builder()
                    .word(csv.getWord())
                    .clue(csv.getClue())
                    .build()
            )
            .collect(Collectors.toSet());

        }
    }
}
