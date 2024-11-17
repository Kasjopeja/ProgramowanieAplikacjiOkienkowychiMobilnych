package com.example.lab3;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ShelterDialogView {

    @FXML
    private TextField nameField;

    @FXML
    private TextField capacityField;

    private Stage dialogStage; // Referencja do okna dialogowego
    private boolean saveClicked = false;

    private String shelterName;
    private int shelterCapacity;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    public String getShelterName() {
        return shelterName;
    }

    public int getShelterCapacity() {
        return shelterCapacity;
    }

    @FXML
    private void handleSave() {
        try {
            shelterName = nameField.getText().trim();
            shelterCapacity = Integer.parseInt(capacityField.getText().trim());

            if (shelterName.isEmpty() || shelterCapacity <= 0) {
                throw new IllegalArgumentException("Invalid input values.");
            }

            saveClicked = true;
            dialogStage.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Error Adding Shelter");
            alert.setContentText("Please enter a valid name and capacity (positive integer).");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
