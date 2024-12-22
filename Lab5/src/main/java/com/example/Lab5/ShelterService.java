package com.example.Lab5;

import io.micrometer.observation.ObservationFilter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class ShelterService {

    private final AnimalShelterRepository shelterRepository;

    public List<AnimalShelter> findAllShelters() {
        return (List<AnimalShelter>) shelterRepository.findAll();
    }

    public AnimalShelter addShelter(AnimalShelter newShelter) {
        return shelterRepository.save(newShelter);
    }

    public boolean deleteShelter(Long id) {
        if (shelterRepository.existsById(id)) {
            shelterRepository.deleteById(id);
            return true;
        }else
            return false;
    }

    public Optional<AnimalShelter> findShelterById(Long id) {
        return shelterRepository.findById(id);
    }
}
