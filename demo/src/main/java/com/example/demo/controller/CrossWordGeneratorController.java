package com.example.demo.controller;

import com.example.demo.model.CrossWordResponse;
import com.example.demo.service.CrossWordGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")

public class CrossWordGeneratorController {

    @Autowired
    CrossWordGeneratorService service;


        @GetMapping("/generateCrossword/{size}")
    public ResponseEntity<?> gridStarter (@PathVariable int size){

        if (size == 5 || size == 6 || size == 7 | size == 8){
            return new ResponseEntity<>(service.gridStarter(size),HttpStatus.OK);
        }

        return new ResponseEntity<>("Invalid size. Please choose either 5,6,7 or 8 for the puzzle grid", HttpStatus.BAD_REQUEST);

    }

}
