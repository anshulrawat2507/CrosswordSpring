package com.example.demo.controller;

import com.example.demo.model.CrossWordResponse;
import com.example.demo.service.CrossWordGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api")

public class CrossWordGeneratorController {

    @Autowired
    CrossWordGeneratorService service;


    @GetMapping("/generateCrossword/{size}")
    public ResponseEntity<?> gridStarter (@PathVariable int size){

        if (size != 5 && size != 7){
            return new ResponseEntity<>("Invalid size. Please choose either 5 or 7 for the puzzle grid", HttpStatus.BAD_REQUEST);
        }

        return service.gridStarter(size);

    }

    @GetMapping("/generateCrossword/{size}/{setId}")
    public ResponseEntity<?> gridStarter(@PathVariable int size, @PathVariable Long setId) {
        if (size != 5 && size != 7) {
            return new ResponseEntity<>("Invalid size. Please choose either 5 or 7 for the puzzle grid", HttpStatus.BAD_REQUEST);
        }
        return service.gridStarter(size, setId);
    }

}
