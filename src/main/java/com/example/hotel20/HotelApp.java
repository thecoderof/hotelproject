package com.example.hotel20;

import javafx.application.Application;
import javafx.stage.Stage;

public class HotelApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            LoginPage loginPage = new LoginPage();
            loginPage.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}