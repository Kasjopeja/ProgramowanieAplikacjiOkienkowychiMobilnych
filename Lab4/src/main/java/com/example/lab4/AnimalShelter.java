package com.example.lab4;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "animal_shelter")
public class AnimalShelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    String shelterName;

    @OneToMany(mappedBy = "shelter", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Animal> animalList = new ArrayList<>();

    @Column(name = "capacity", nullable = false)
    int maxCapacity;

    AnimalShelter(String shelterName, int maxCapacity) {
        this.shelterName = shelterName.toLowerCase();
        this.maxCapacity = maxCapacity;
    }

    public void addAnimal(Animal animal) {
        if (animalList.size() >= maxCapacity) {
            throw new IllegalStateException("No more room in the shelter available");
        }

        // Sprawdzanie, czy zwierzę o takich samych parametrach już istnieje w schronisku
        for (Animal existingAnimal : animalList) {
            if (existingAnimal.equals(animal)) { // Użycie equals zamiast compareTo
                System.out.println(animal.species + " " + animal.name + " with the same parameters is already present in the shelter.");
                return;
            }
        }

        // Dodanie zwierzęcia do listy
        animalList.add(animal);
        System.out.println(animal.species + " " + animal.name + " was added to the shelter.");
    }


    public void removeAnimal(Animal animal) {
        if (animalList.contains(animal)) {
            animalList.remove(animal);
            System.out.println(animal.species + " " + animal.name + " was removed from the shelter.");
        }
    }
}
