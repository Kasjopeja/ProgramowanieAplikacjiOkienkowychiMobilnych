package com.example.lab4;

import java.util.*;

public class ShelterManager {
    Map<String, AnimalShelter> shelters;

    public ShelterManager() {
        this.shelters = new LinkedHashMap<>(); // Zmiana HashMap na LinkedHashMap
    }

    public void addToShelter(String name, Animal animal) {
        this.shelters.get(name).addAnimal(animal);
    }

    public void addShelter(String name, int capacity) {
        String lowerCaseName = name.toLowerCase();
        if (shelters.containsKey(lowerCaseName)) {
            throw new IllegalArgumentException("Shelter with the same name already exists.");
        }
        AnimalShelter shelter = new AnimalShelter(name, capacity);
        shelters.put(lowerCaseName, shelter);
    }

    public AnimalShelter getShelterByName(String shelterName) {
        return shelters.get(shelterName);
    }


    public List<AnimalShelter> getAllShelters() {
        return new ArrayList<>(shelters.values());
    }

}