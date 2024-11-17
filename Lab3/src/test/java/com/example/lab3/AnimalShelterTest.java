package com.example.lab3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnimalShelterTest {

    private AnimalShelter shelter;

    @BeforeEach
    void setUp() {
        shelter = new AnimalShelter("Happy Paws", 3);
        shelter.addAnimal(new Animal("Buddy", "Dog", 3, 200.0, 20.0, AnimalCondition.Zdrowe));
        shelter.addAnimal(new Animal("Mittens", "Cat", 2, 100.0, 5.0, AnimalCondition.Chore));
    }

    @Test
    void testAddAnimal() {
        // Dodanie nowego zwierzęcia
        shelter.addAnimal(new Animal("Polly", "Parrot", 1, 50.0, 0.5, AnimalCondition.Kwarantanna));
        assertEquals(3, shelter.getAnimalList().size(), "Animal should be added successfully.");

        // Próba przekroczenia pojemności
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            shelter.addAnimal(new Animal("Rocky", "Dog", 4, 180.0, 25.0, AnimalCondition.Zdrowe));
        });
        assertEquals("No more room in the shelter available", exception.getMessage());
    }

    @Test
    void testRemoveAnimal() {
        // Usuwanie istniejącego zwierzęcia
        Animal animal = shelter.getAnimalList().get(0);
        shelter.removeAnimal(animal);
        assertEquals(1, shelter.getAnimalList().size(), "Animal should be removed successfully.");
        assertFalse(shelter.getAnimalList().contains(animal), "Animal should no longer be in the shelter.");
    }

    @Test
    void testGetAnimal() {
        Animal animal = shelter.getAnimalList().get(0);
        shelter.getAnimal(animal);
        assertFalse(shelter.getAnimalList().contains(animal), "Animal should be removed from the shelter.");
        assertEquals(AnimalCondition.WTrakcieAdopcji, animal.getCondition(), "Animal condition should be updated.");
    }

    @Test
    void testChangeCondition() {
        Animal animal = shelter.getAnimalList().get(1);
        shelter.changeCondition(animal, AnimalCondition.Zdrowe);
        assertEquals(AnimalCondition.Zdrowe, animal.getCondition(), "Animal condition should be updated.");
    }

    @Test
    void testChangeAge() {
        Animal animal = shelter.getAnimalList().get(0);
        shelter.changeAge(animal, 5);
        assertEquals(5, animal.getAge(), "Animal age should be updated.");
    }

    @Test
    void testSortByName() {
        shelter.sortByName();
        assertEquals("buddy", shelter.getAnimalList().get(0).getName(), "Animals should be sorted by name.");
        assertEquals("mittens", shelter.getAnimalList().get(1).getName(), "Animals should be sorted by name.");
    }

    @Test
    void testSortByPrice() {
        shelter.sortByPrice();
        assertEquals("mittens", shelter.getAnimalList().get(0).getName(), "Animals should be sorted by price.");
        assertEquals("buddy", shelter.getAnimalList().get(1).getName(), "Animals should be sorted by price.");
    }

    @Test
    void testSearch() {
        Animal animal = shelter.getAnimalList().get(0);
        shelter.search("Buddy");
        assertTrue(animal.getName().equalsIgnoreCase("Buddy"), "Animal should be found by name.");
    }

    @Test
    void testSearchPartial() {
        shelter.searchPartial("itt");
        assertEquals("mittens", shelter.getAnimalList().get(1).getName(), "Animal should be found by partial name.");
    }

    @Test
    void testCountByCondition() {
        shelter.addAnimal(new Animal("Rocky", "Dog", 4, 180.0, 25.0, AnimalCondition.Zdrowe));
        shelter.countByCondition(AnimalCondition.Zdrowe);
        assertEquals(2, shelter.getAnimalList().stream()
                .filter(a -> a.getCondition() == AnimalCondition.Zdrowe)
                .count(), "Count by condition should return correct value.");
    }

    @Test
    void testSummary() {
        shelter.summary();
        assertEquals(2, shelter.getAnimalList().size(), "Summary should display all animals.");
    }

    @Test
    void testMax() {
        shelter.addAnimal(new Animal("Rocky", "Dog", 4, 300.0, 25.0, AnimalCondition.Zdrowe));
        shelter.max();
        assertEquals("rocky", shelter.getAnimalList().get(2).getName(), "Max animal by price should be 'Rocky'.");
    }
}
