package com.example.Lab5;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "animal")
@NoArgsConstructor
@AllArgsConstructor
public class Animal{

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
    @JsonBackReference
    private AnimalShelter shelter;

}

