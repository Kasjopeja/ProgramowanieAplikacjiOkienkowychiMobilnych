package com.example.Lab5;

import io.micrometer.observation.ObservationFilter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final AnimalShelterRepository shelterRepository;

    public Optional<Animal> addAnimal(Animal animal, Long shelterId) {
        Optional<AnimalShelter> optShelter = shelterRepository.findById(shelterId);
        if (optShelter.isEmpty()) {
            return Optional.empty();
        }

        AnimalShelter shelter = optShelter.get();

        if (shelter.getAnimalList().size() >= shelter.getMaxCapacity()) {
            return Optional.empty();
        }

        animal.setShelter(shelter);
        Animal savedAnimal = animalRepository.save(animal);

        shelterRepository.save(shelter);

        return Optional.of(savedAnimal);
    }

    public boolean deleteAnimal(Long id) {
        if (animalRepository.existsById(id)) {
            animalRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public Optional<Animal> findAnimalById(Long id) {
        return animalRepository.findById(id);
    }

}
