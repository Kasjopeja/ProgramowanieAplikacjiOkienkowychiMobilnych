package com.example.lab3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ShelterApplication extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        showHelloView();
    }

    public void showHelloView() throws Exception
    {
        FXMLLoader fxmlLoader = new FXMLLoader(ShelterApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        ShelterController controller = fxmlLoader.getController();
        controller.setMainApp(this);
        primaryStage.setTitle("Hello!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showLoginScreen() throws Exception
    {
        FXMLLoader fxmlLoader = new FXMLLoader(ShelterApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        ShelterController controller = fxmlLoader.getController();
        controller.setMainApp(this);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showUserScreen() throws Exception
    {
        FXMLLoader fxmlLoader = new FXMLLoader(ShelterApplication.class.getResource("user-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        primaryStage.setTitle("User View");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showAdminScreen() throws Exception
    {
        FXMLLoader fxmlLoader = new FXMLLoader(ShelterApplication.class.getResource("admin-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        primaryStage.setTitle("Admin View");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}