package com.example.lab4;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javax.swing.*;
import java.util.List;

public class UserController {

    private ShelterApplication mainApp;

    @FXML
    private SwingNode animalsNode;

    @FXML
    private SwingNode shelterSelectionNode;

    @FXML
    private TextField filterTextBox;

    @FXML
    private ComboBox<String> stateComboBox;

    private List<Animal> filteredAnimals;
    private ExampleData data;
    private AnimalShelter currentShelter;

    @FXML
    public void initialize() {
        data = new ExampleData();
        data.load();

        stateComboBox.getItems().addAll("Zdrowe", "Chore", "Kwarantanna", "WTrakcieAdopcji");
        stateComboBox.setValue("Zdrowe"); // Ustaw wartość domyślną
        currentShelter = data.getDeafultShelter(); // Domyślne schronisko
        createShelterSelection(shelterSelectionNode);
        createAnimalJTable(animalsNode, currentShelter);
    }

    public void setMainApp(ShelterApplication mainApp) {
        this.mainApp = mainApp;
    }

    private void createShelterSelection(SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {
            List<AnimalShelter> shelters = data.shelterManager.getAllShelters();

            JComboBox<String> shelterComboBox = new JComboBox<>();
            for (AnimalShelter shelter : shelters) {
                shelterComboBox.addItem(shelter.getShelterName());
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

    @FXML
    private void handleFilterEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            String filterText = filterTextBox.getText().toLowerCase();

            // Filtrowanie zwierząt na podstawie nazwy lub gatunku
            filteredAnimals = currentShelter.getAnimalList().stream()
                    .filter(animal -> animal.getName().toLowerCase().contains(filterText) ||
                            animal.getSpecies().toLowerCase().contains(filterText))
                    .toList();

            // Odśwież tabelę z wynikami filtrowania
            refreshAnimalTable();
        }
    }

    @FXML
    private void handleStateChange() {
        String selectedState = stateComboBox.getValue();

        // Filtrowanie zwierząt na podstawie stanu
        filteredAnimals = currentShelter.getAnimalList().stream()
                .filter(animal -> animal.getCondition().toString().equalsIgnoreCase(selectedState))
                .toList();

        // Odśwież tabelę z wynikami filtrowania
        refreshAnimalTable();
    }

    private void refreshAnimalTable() {
        SwingUtilities.invokeLater(() -> {
            if (filteredAnimals == null || filteredAnimals.isEmpty()) {
                animalsNode.setContent(new JLabel("No animals found."));
                return;
            }

            GenericTableModel<Animal> animalTableModel = new GenericTableModel<>(Animal.class);
            animalTableModel.setData(filteredAnimals);

            JTable animalTable = new JTable(animalTableModel);
            animalTable.setFillsViewportHeight(true);

            JScrollPane scrollPane = new JScrollPane(animalTable);

            animalsNode.setContent(scrollPane);
        });
    }
}
