package com.example.Lab5;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalShelterRepository extends CrudRepository<AnimalShelter, Long> {
}
