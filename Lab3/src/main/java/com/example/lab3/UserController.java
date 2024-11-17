package com.example.lab3;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;

import javax.swing.*;
import java.util.List;

public class UserController {

    private ShelterApplication mainApp;

    @FXML
    private SwingNode animalsNode;

    @FXML
    private SwingNode shelterSelectionNode;

    private ExampleData data;

    @FXML
    public void initialize() {
        data = new ExampleData();
        data.load();

        createShelterSelection(shelterSelectionNode);
        createAnimalJTable(animalsNode, data.getDeafultShelter());
    }

    public void setMainApp(ShelterApplication mainApp) {
        this.mainApp = mainApp;
    }

    private void createShelterSelection(SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {
            List<AnimalShelter> shelters = data.shelterManager.getAllShelters();

            JComboBox<String> shelterComboBox = new JComboBox<>();
            for (AnimalShelter shelter : shelters) {
                shelterComboBox.addItem(shelter.getName());
            }

            shelterComboBox.addActionListener(e -> {
                String selectedShelterName = (String) shelterComboBox.getSelectedItem();
                AnimalShelter selectedShelter = data.shelterManager.getShelterByName(selectedShelterName);
                createAnimalJTable(animalsNode, selectedShelter);
            });

            swingNode.setContent(shelterComboBox);
        });
    }

    private void createAnimalJTable(SwingNode swingNode, AnimalShelter shelter) {
        SwingUtilities.invokeLater(() -> {
            GenericTableModel<Animal> animalTableModel = new GenericTableModel<>(Animal.class);
            animalTableModel.setData(shelter.getAnimalList());

            JTable animalTable = new JTable(animalTableModel);
            animalTable.setFillsViewportHeight(true);

            JScrollPane scrollPane = new JScrollPane(animalTable);

            swingNode.setContent(scrollPane);
        });
    }
}
