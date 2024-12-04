package com.example.lab4;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "animals")
@NoArgsConstructor
public class Animal implements Comparable<Animal>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "species", nullable = false)
    String species;

    @Column(name = "age", nullable = false)
    int age;

    @Column(name = "price", nullable = false)
    double price;

    @Column(name = "weight", nullable = false)
    double weight;

    @Enumerated(EnumType.STRING)  // Przechowywanie wartości enum w formie String
    @Column(name = "condition", nullable = false)
    AnimalCondition condition;

    @ManyToOne
    @JoinColumn(name = "shelter_id", nullable = false)
    private AnimalShelter shelter;

    public Animal(String name, String species, int age, double price, double weight, AnimalCondition condition) {
        this.name = name.toLowerCase();
        this.species = species.toLowerCase();
        this.age = age;
        this.price = price;
        this.weight = weight;
        this.condition = condition;
    }

    public void print() {
        System.out.println("Name: " + this.name + "\nSpecies: " + this.species + "\nAge: " + this.age + "\nPrice: " + this.price + "\nWeight: " + this.weight + "\nCondition: " + this.condition);
    }

    @Override
    public int compareTo(Animal other) {
        int nameCompare = this.name.compareTo(other.name);
        if (nameCompare != 0) {
            return nameCompare;
        }

        int speciesCompare = this.species.compareTo(other.species);
        if (speciesCompare != 0) {
            return speciesCompare;
        }

        return Integer.compare(this.age, other.age);
    }
}

