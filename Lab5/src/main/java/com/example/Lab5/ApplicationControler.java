package com.example.Lab5;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApplicationControler {

    private final AnimalService animalService;

    public ApplicationControler(AnimalService animalService) {
        this.animalService = animalService;
    }


    // 1. POST /api/animal
    @PostMapping("/animal")
    public ResponseEntity<Animal> addAnimal(@RequestBody Animal animal,
                                            @RequestParam Long shelterId) {
        Optional<Animal> saved = animalService.addAnimal(animal, shelterId);
        if (saved.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(saved.get());
        } else {
            // np. 400 gdy brak miejsca, albo 404 gdy brak schroniska
            return ResponseEntity.badRequest().build();
        }
    }


}