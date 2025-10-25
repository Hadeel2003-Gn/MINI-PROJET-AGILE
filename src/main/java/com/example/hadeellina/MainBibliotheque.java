package com.example.hadeellina;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainBibliotheque extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tp1gd/bibliotheque.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 650);
        stage.setTitle("Gestion Bibliothèque - Clients et Emprunts");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
