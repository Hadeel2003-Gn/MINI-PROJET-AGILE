package com.example.hadeellina;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger la page d'authentification client
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tp1gd/auth-client.fxml"));
            Scene scene = new Scene(loader.load(), 600, 600);

            primaryStage.setTitle("Espace Client - Bibliothèque");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            // En cas d'erreur, ouvrir directement la bibliothèque
            ouvrirBibliothequeDirectement(primaryStage);
        }
    }

    private void ouvrirBibliothequeDirectement(Stage stage) {
        try {
            MainBibliotheque mainBiblio = new MainBibliotheque();
            mainBiblio.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
