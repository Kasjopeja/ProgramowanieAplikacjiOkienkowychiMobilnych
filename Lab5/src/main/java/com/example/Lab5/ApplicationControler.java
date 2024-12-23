package com.example.Lab5;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApplicationControler {

    private final AnimalService animalService;
    private final ShelterService shelterService;
    private final RatingService ratingService;

    public ApplicationControler(AnimalService animalService, ShelterService shelterService, RatingService ratingService) {
        this.animalService = animalService;
        this.shelterService = shelterService;
        this.ratingService = ratingService;
    }


    // 1. POST /api/animal
    @PostMapping("/animal")
    public ResponseEntity<Animal> addAnimal(@RequestBody Animal animal,
                                            @RequestParam Long shelterId) {
        Optional<Animal> saved = animalService.addAnimal(animal, shelterId);
        if (saved.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(saved.get());
        } else {

            return ResponseEntity.badRequest().build();
        }
    }

    // 2. DELETE api/animal/{id}
    @DeleteMapping("/animal/{id}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable Long id) {
        boolean deleted = animalService.deleteAnimal(id);
        if (deleted) {
            return ResponseEntity.noContent().build();  // 204
        } else {
            return ResponseEntity.notFound().build();   // 404
        }
    }

    // 3. GET api/animal/{id}"
    @GetMapping("/animal/{id}")
    public ResponseEntity<Animal> getAnimalById(@PathVariable Long id) {
        return animalService.findAnimalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. GET api/animalshelter/csv"
    @GetMapping(value = "/animalshelter/csv", produces = "text/csv")
    public ResponseEntity<String> getSheltersCsv() {
        List<AnimalShelter> shelters = shelterService.findAllShelters();

        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("id,name,capacity,currentAnimals\n"); // nagłówki kolumn

        for (AnimalShelter shelter : shelters) {
            csvBuilder.append(shelter.getId()).append(",");
            csvBuilder.append(shelter.getShelterName()).append(",");
            csvBuilder.append(shelter.getMaxCapacity()).append(",");
            // ile aktualnie jest zwierząt
            csvBuilder.append(shelter.getAnimalList().size()).append("\n");
        }

        return ResponseEntity
                .ok(csvBuilder.toString());
    }

    // 5. GET api/sheltermanager
    @GetMapping("/sheltermanager")
    public ResponseEntity<List<AnimalShelter>> getAllShelters() {
        List<AnimalShelter> shelters = shelterService.findAllShelters();
        return ResponseEntity.ok(shelters);
    }

    //6. POST api/animalshelter
    @PostMapping("/animalshelter")
    public ResponseEntity<AnimalShelter> createShelter(@RequestBody AnimalShelter newShelter) {
        AnimalShelter saved = shelterService.addShelter(newShelter);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 7. DELETE api/animalshelter/{id}
    @DeleteMapping("/animalshelter/{id}")
    public ResponseEntity<Void> deleteShelter(@PathVariable Long id) {
        boolean deleted = shelterService.deleteShelter(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 8. GET /animalshelter/{id}/animal
    @GetMapping("/animalshelter/{id}/animal")
    public ResponseEntity<List<Animal>> getAllAnimalsInShelter(@PathVariable Long id) {
        return shelterService.findShelterById(id)
                .map(shelter -> ResponseEntity.ok(shelter.getAnimalList()))
                .orElse(ResponseEntity.notFound().build());
    }

    // 9.GET /animalshelter/{id}/fill
    @GetMapping("/animalshelter/{id}/fill")
    public ResponseEntity<Double> getShelterFill(@PathVariable Long id) {
        return shelterService.findShelterById(id)
                .map(shelter -> {
                    int current = shelter.getAnimalList().size();
                    int capacity = shelter.getMaxCapacity();

                    double fillPercent = (capacity == 0)
                            ? 0.0
                            : 100.0 * current / capacity;

                    return ResponseEntity.ok(fillPercent);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 10. POST api/rating
    @PostMapping("/rating")
    public ResponseEntity<Rating> addRating(@RequestBody Rating rating) {
        Rating saved = ratingService.saveRating(rating);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    // GET api/rating
    @GetMapping("/rating")
    public ResponseEntity<Iterable<Rating>> getAllRatings() {
        Iterable<Rating> ratings = ratingService.getAllRatings();
        return ResponseEntity.ok(ratings);
    }

    // DELETE api/rating
    @DeleteMapping("/rating/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        boolean deleted = ratingService.deleteRating(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}