package com.example.lab4;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import lombok.Setter;

public class LoginController {

    //---------
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
    //-----------

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button LoginButton;

    @Setter
    private ShelterApplication mainApp;

    @FXML
    private void handleLogin() throws Exception {
        if (usernameField.getText().equals("user") && passwordField.getText().equals("userpass")) {
            //mainApp.showUserScreen();
        }
        else if (usernameField.getText().equals("admin") && passwordField.getText().equals("adminpass")) {
            mainApp.showAdminScreen();
        }
        else {
            showAlert("Invalid username or password!");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}