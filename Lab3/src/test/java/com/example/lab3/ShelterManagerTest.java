package com.example.lab3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShelterManagerTest {

    private ShelterManager shelterManager;

    @BeforeEach
    void setUp() {
        shelterManager = new ShelterManager();
        shelterManager.addShelter("Happy Paws", 5);
        shelterManager.addShelter("Cozy Tails", 3);
    }

    @Test
    void testAddShelter() {
        shelterManager.addShelter("New Shelter", 10);
        AnimalShelter newShelter = shelterManager.getShelterByName("new shelter");
        assertNotNull(newShelter, "New shelter should be added successfully.");
        assertEquals(10, newShelter.getMaxCapacity(), "New shelter should have the correct capacity.");
    }

    @Test
    void testRemoveShelter() {
        shelterManager.removeShelter("Happy Paws");
        assertNull(shelterManager.getShelterByName("Happy Paws"), "Shelter should be removed.");
    }

    @Test
    void testGetShelterByName() {
        AnimalShelter shelter = shelterManager.getShelterByName("happy paws");
        assertNotNull(shelter, "Shelter should exist.");
        assertEquals(5, shelter.getMaxCapacity(), "Shelter should have correct capacity.");
    }

    @Test
    void testGetAllShelters() {
        List<AnimalShelter> shelters = shelterManager.getAllShelters();
        assertEquals(2, shelters.size(), "There should be two shelters.");
    }

}
