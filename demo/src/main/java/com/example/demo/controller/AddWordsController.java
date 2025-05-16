package com.example.demo.controller;

import com.example.demo.service.AddWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class AddWordsController {

    @Autowired
    AddWordsService service;

        @PostMapping(value = "/uploadWords", consumes = "multipart/form-data")
    public ResponseEntity<?> addWordsToDatabase(@RequestPart("file")MultipartFile file){

        try {
            String message = service.addWordsToDatabase (file);
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
