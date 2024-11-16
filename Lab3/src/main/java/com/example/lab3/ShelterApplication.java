package com.example.lab3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.embed.swing.SwingNode;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ShelterApplication extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        //showHelloView();
        showLoginScreen();
    }

    public void showHelloView() throws Exception
    {
        FXMLLoader fxmlLoader = new FXMLLoader(ShelterApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        LoginController controller = fxmlLoader.getController();
        controller.setMainApp(this);
        primaryStage.setTitle("Hello!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showLoginScreen() throws Exception
    {
        FXMLLoader fxmlLoader = new FXMLLoader(ShelterApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 200);
        LoginController controller = fxmlLoader.getController();
        controller.setMainApp(this);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showUserScreen() throws Exception
    {
        FXMLLoader fxmlLoader = new FXMLLoader(ShelterApplication.class.getResource("user-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        UserController controller = fxmlLoader.getController();
        controller.setMainApp(this);
        primaryStage.setTitle("User View");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showAdminScreen() throws Exception
    {
        FXMLLoader fxmlLoader = new FXMLLoader(ShelterApplication.class.getResource("admin-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        AdminController controller = fxmlLoader.getController();
        controller.setMainApp(this);
        primaryStage.setTitle("Admin View");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}