package com.example.lab3;

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


    public void removeShelter(String name) {
        shelters.remove(name.toLowerCase());
    }

    public void findEmpty() {
        List<String> names = new ArrayList<>();
        for (Map.Entry<String, AnimalShelter> entry : shelters.entrySet()) {
            if (!names.contains(entry.getKey())) {
                names.add(entry.getKey());
            }
        }
    }

    public void summary() {
        for (Map.Entry<String, AnimalShelter> entry : shelters.entrySet()) {
            System.out.println(entry.getValue().shelterName + " is " +
                    String.format("%.2f", (double) (entry.getValue().animalList.size()) / (double) (entry.getValue().maxCapacity) * 100) +
                    "% full");
        }
    }

    public AnimalShelter getShelterByName(String shelterName) {
        return shelters.get(shelterName);
    }


    public List<AnimalShelter> getAllShelters() {
        return new ArrayList<>(shelters.values());
    }

}
