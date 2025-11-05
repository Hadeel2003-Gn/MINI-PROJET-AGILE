package com.example.hadeellina;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger directement la page d'authentification client
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tp1gd/auth-client.fxml"));
            Scene scene = new Scene(loader.load(), 600, 600);

            primaryStage.setTitle("📚 Bibliothèque - Espace Client");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de l'interface d'authentification");
        }
    }

    private void showError(String message) {
        System.err.println("❌ " + message);
    }

    public static void main(String[] args) {
        System.out.println("🚀 Lancement de l'application Bibliothèque...");
        launch(args);
    }
}