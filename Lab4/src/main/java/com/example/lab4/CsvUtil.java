package com.example.lab4;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class CsvUtil {

    // Eksport danych schroniska do pliku CSV
    public static void exportShelterToCsv(String fileName, ObservableList<AnimalShelter> shelters) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Nagłówki
            writer.write("Shelter Name,Contact,Max Capacity");
            writer.newLine();

            // Dane schroniska
            for (AnimalShelter shelter : shelters){
                writer.write(String.format("%d,%s,%d",
                        shelter.getId(),
                        shelter.getShelterName(),
                        shelter.getMaxCapacity()
                ));
                writer.newLine();
            }

            System.out.println("Dane schroniska wyeksportowane do " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Eksport listy zwierząt do pliku CSV (ObservableList)
    public static void exportAnimalsToCsv(String fileName, List<Animal> animals) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Nagłówki
            writer.write("Name,Species,Condition,Age,Price,Weight");
            writer.newLine();

            // Dane zwierząt
            for (Animal animal : animals) {
                writer.write(String.format("%d,%s,%s,%s,%d,%.2f,%.2f",
                        animal.getId(),
                        animal.getName(),
                        animal.getSpecies(),
                        animal.getCondition(),
                        animal.getAge(),
                        animal.getPrice(),
                        animal.getWeight()
                ));
                writer.newLine();
            }

            System.out.println("Lista zwierząt wyeksportowana do " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    // Import danych schroniska z pliku CSV
//    public static ObservableList<AnimalShelter> importShelterFromCsv(String fileName) {
//        ObservableList<AnimalShelter> shelters = FXCollections.observableArrayList();
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
//            String line = reader.readLine(); // Skip header
//            if (line == null) {
//                throw new IOException("The file is empty!");
//            }
//
//            while ((line = reader.readLine()) != null) {
//                String[] shelterData = line.split(",");
//                if (shelterData.length == 3) { // Ensure the correct number of fields
//                    shelters.add(new AnimalShelter(
//                            shelterData[0], // name
//                            null, // animals to be set later
//                            Integer.parseInt(shelterData[2]), // maxCapacity
//                            shelterData[1] // contact
//                    ));
//                } else {
//                    System.err.println("Invalid data format: " + line);
//                }
//            }
//
//            if (!shelters.isEmpty()) {
//                System.out.println("First shelter imported: " + shelters.get(0).getName());
//            } else {
//                System.err.println("No valid shelters were imported.");
//            }
//        } catch (IOException | NumberFormatException e) {
//            e.printStackTrace();
//        }
//
//        return shelters;
//    }
//
//
//    // Import listy zwierząt z pliku CSV (ObservableList)
//    public static ObservableList<Animal> importAnimalsFromCsv(String fileName, AnimalShelter shelter) {
//        ObservableList<Animal> animals = FXCollections.observableArrayList();
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
//            String line = reader.readLine(); // Pomijanie nagłówka
//            if (line == null) {
//                throw new IOException("Plik jest pusty!");
//            }
//
//            while ((line = reader.readLine()) != null) {
//                String[] animalData = line.split(",");
//                Animal animal = new Animal(
//                        animalData[0], // name
//                        animalData[1], // species
//                        AnimalCondition.valueOf(animalData[2]), // condition
//                        Integer.parseInt(animalData[3]), // age
//                        Double.parseDouble(animalData[4]), // price
//                        Double.parseDouble(animalData[5]) // weight
//                );
//                animal.setShelter(shelter);
//                animals.add(animal);
//            }
//
//            System.out.println("Lista zwierząt zaimportowana z " + fileName);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return animals;
//    }
}