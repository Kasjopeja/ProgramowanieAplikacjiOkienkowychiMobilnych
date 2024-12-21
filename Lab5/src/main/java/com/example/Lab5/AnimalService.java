package com.example.Lab5;

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

        shelter.getAnimalList().add(savedAnimal);
        shelterRepository.save(shelter);

        return Optional.of(savedAnimal);
    }
}
