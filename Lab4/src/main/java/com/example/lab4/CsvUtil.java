package com.example.lab4;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class CsvUtil {

    // Eksport danych schroniska do pliku CSV
    public static void exportShelterToCsv(String fileName, List<AnimalShelter> shelters) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Nagłówki
            writer.write("ID;Shelter Name;Max Capacity");
            writer.newLine();

            // Dane schroniska
            for (AnimalShelter shelter : shelters){
                writer.write(String.format("%d;%s;%d",
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
            writer.write("ID;Name;Species;Condition;Age;Price;Weight");
            writer.newLine();

            // Dane zwierząt
            for (Animal animal : animals) {
                writer.write(String.format("%d;%s;%s;%s;%d;%.2f;%.2f",
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

    // Import danych schroniska z pliku CSV
    public static List<AnimalShelter> importShelterFromCsv(String fileName, List<AnimalShelter> shelters) {

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line = reader.readLine(); // Skip header
            if (line == null) {
                throw new IOException("The file is empty!");
            }

            while ((line = reader.readLine()) != null) {
                String[] shelterData = line.split(";");
                if (shelterData.length == 3) { // Ensure the correct number of fields
                    shelters.add(new AnimalShelter(
                            Integer.parseInt(shelterData[0]), //id
                            shelterData[1], // name
                            Integer.parseInt(shelterData[2])// maxCapacity
                    ));
                } else {
                    System.err.println("Invalid data format: " + line);
                }
            }

            if (!shelters.isEmpty()) {
                System.out.println("First shelter imported: " + shelters.get(0).getShelterName());
            } else {
                System.err.println("No valid shelters were imported.");
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return shelters;
    }


    // Import listy zwierząt z pliku CSV (ObservableList)
    public static List<Animal> importAnimalsFromCsv(String fileName) throws IOException {
        List<Animal> animals = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        String line;
        boolean isFirstLine = true;

        while ((line = reader.readLine()) != null) {
            if (isFirstLine) {
                isFirstLine = false; // Skip header
                continue;
            }

            String[] fields = line.split(";");
            if (fields.length < 7) {
                throw new IllegalArgumentException("Invalid CSV format. Expected 7 fields.");
            }

            // Validate and parse fields
            String idField = fields[0].trim();
            if (idField.isEmpty()) {
                throw new IllegalArgumentException("ID field cannot be empty");
            }
            int id = Integer.parseInt(idField);

            String name = fields[1].trim();
            String species = fields[2].trim();
            AnimalCondition condition = AnimalCondition.valueOf(fields[3].trim());

            String ageField = fields[4].trim();
            if (ageField.isEmpty()) {
                throw new IllegalArgumentException("Age field cannot be empty");
            }
            int age = Integer.parseInt(ageField);

            double price = Double.parseDouble(fields[5].trim().replace(",", "."));
            double weight = Double.parseDouble(fields[6].trim().replace(",", "."));

            // Create Animal object
            animals.add(new Animal(id, name, species, age, price, weight, condition));

        }

        return animals;
    }
}
