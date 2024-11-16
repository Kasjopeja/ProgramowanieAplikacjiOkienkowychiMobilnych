package com.example.lab3;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;

import javax.swing.*;
import java.util.List;

public class UserController {

    private ShelterApplication mainApp;

    @FXML
    private SwingNode sheltersNode;

    @FXML
    private SwingNode animalsNode;


    @FXML
    public void initialize() {
        ExampleData data = new ExampleData();
        data.load();
        createShelterJTable(sheltersNode, data);
        createAnimalJTable(animalsNode, data);
    }

    public void setMainApp(ShelterApplication mainApp) {
        this.mainApp = mainApp;
    }

    private void createShelterJTable(SwingNode swingNode, ExampleData data) {
        SwingUtilities.invokeLater(() -> {
            List<AnimalShelter> shelters = data.shelterManager.getAllShelters();

            GenericTableModel<AnimalShelter> animalTableModel = new GenericTableModel<>(AnimalShelter.class);
            animalTableModel.setExcludedColumns(List.of("animalList")); // Wykluczenie kolumny
            animalTableModel.setData(shelters);

            JTable animalShelterTable = new JTable(animalTableModel);
            animalShelterTable.setFillsViewportHeight(true);

            JScrollPane scrollPane = new JScrollPane(animalShelterTable);

            swingNode.setContent(scrollPane);

        });
    }

    private void createAnimalJTable(SwingNode swingNode, ExampleData data) {
        SwingUtilities.invokeLater(() -> {
            AnimalShelter shelter = data.getShelter();

            GenericTableModel<Animal> animalTableModel = new GenericTableModel<>(Animal.class);
            animalTableModel.setData(shelter.getAnimalList());

            JTable animalTable = new JTable(animalTableModel);
            animalTable.setFillsViewportHeight(true);

            JScrollPane scrollPane = new JScrollPane(animalTable);

            swingNode.setContent(scrollPane);
        });
    }

}
