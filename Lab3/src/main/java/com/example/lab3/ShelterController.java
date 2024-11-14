package com.example.lab3;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ShelterController {

    private ShelterApplication mainApp;

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    public void setMainApp(ShelterApplication mainApp) {
        this.mainApp = mainApp;
    }
}