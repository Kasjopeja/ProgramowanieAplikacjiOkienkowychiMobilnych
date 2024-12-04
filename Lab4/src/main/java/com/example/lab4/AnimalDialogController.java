package com.example.lab4;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AnimalDialogController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField speciesField;

    @FXML
    private TextField ageField;

    @FXML
    private TextField priceField;

    @FXML
    private ComboBox<AnimalCondition> conditionComboBox;

    private Animal animal; // Referencja do edytowanego zwierzęcia
    private Stage dialogStage; // Referencja do okna dialogowego
    private boolean saveClicked = false;

    @FXML
    public void initialize() {
        // Inicjalizacja ComboBox z możliwymi wartościami
        conditionComboBox.getItems().setAll(AnimalCondition.values());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;

        // Wypełnij pola formularza danymi zwierzęcia
        nameField.setText(animal.name);
        speciesField.setText(animal.species);
        ageField.setText(String.valueOf(animal.age));
        priceField.setText(String.valueOf(animal.price));
        conditionComboBox.setValue(animal.condition);
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        try {
            // Aktualizuj dane zwierzęcia na podstawie wartości z formularza
            animal.name = nameField.getText();
            animal.species = speciesField.getText();
            animal.age = Integer.parseInt(ageField.getText());
            animal.price = Double.parseDouble(priceField.getText());
            animal.condition = conditionComboBox.getValue();

            saveClicked = true;
            dialogStage.close(); // Zamknij okno dialogowe
        } catch (Exception e) {
            // Obsługa błędów walidacji
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Please check the input fields.");
            alert.setContentText("Age and Price must be numbers.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close(); // Zamknij okno dialogowe bez zapisania zmian
    }
}
