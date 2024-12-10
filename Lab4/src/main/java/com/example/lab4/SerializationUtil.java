package com.example.lab4;

import java.io.*;
import java.util.List;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SerializationUtil {

    // Save shelters and animals to a binary file
    public static void saveToFile(String fileName, List<AnimalShelter> shelters) {
        // Convert the list to a serializable version (ArrayList)
        List<AnimalShelter> serializableShelters = new ArrayList<>(shelters);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(serializableShelters);
//            System.out.println("Dane zapisano pomyślnie do " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load shelters and animals from a binary file
    public static List<AnimalShelter> loadFromFile(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (List<AnimalShelter>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load file: " + e.getMessage());
            return null;
        }
    }

}
