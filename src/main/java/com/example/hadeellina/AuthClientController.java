package com.example.hadeellina;

import com.example.tp1gd.models.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AuthClientController {

    // Champs de connexion
    @FXML private TextField loginEmail;
    @FXML private PasswordField loginPassword;
    @FXML private Label loginMessage;

    // Champs d'inscription
    @FXML private TextField regNom;
    @FXML private TextField regPrenom;
    @FXML private TextField regTelephone;
    @FXML private TextField regEmail;
    @FXML private TextField regAdresse;
    @FXML private PasswordField regPassword;
    @FXML private PasswordField regConfirmPassword;
    @FXML private Label regMessage;

    private ApplicationState appState = ApplicationState.getInstance();

    @FXML
    private void initialize() {
        // Focus initial sur l'email de connexion
        loginEmail.requestFocus();
    }

    // ==================== CONNEXION ====================
    @FXML
    private void onLogin() {
        String email = loginEmail.getText().trim().toLowerCase();
        String password = loginPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showLoginMessage("Veuillez remplir tous les champs", true);
            return;
        }

        try {
            if (appState.connecterClient(email, password)) {
                showSuccess("✅ Connexion réussie ! Bienvenue " + appState.getClientActuel().getNom());
                ouvrirGestionBibliotheque();
            } else {
                showLoginMessage("❌ Email ou mot de passe incorrect", true);
                loginPassword.clear();
                loginPassword.requestFocus();
            }
        } catch (Exception e) {
            showLoginMessage("❌ Erreur: " + e.getMessage(), true);
        }
    }

    // ==================== INSCRIPTION ====================
    @FXML
    private void onRegister() {
        // Validation des champs
        if (!validerFormulaireInscription()) {
            return;
        }

        try {
            // Vérifier si l'email existe déjà
            if (emailExisteDeja(regEmail.getText().trim())) {
                showRegMessage("❌ Cet email est déjà utilisé", true);
                regEmail.requestFocus();
                return;
            }

            // Vérifier si le téléphone existe déjà
            if (telephoneExisteDeja(regTelephone.getText().trim())) {
                showRegMessage("❌ Ce numéro de téléphone est déjà utilisé", true);
                regTelephone.requestFocus();
                return;
            }

            // Créer le nouveau client
            Client nouveauClient = creerNouveauClient();
            appState.getClients().add(nouveauClient);
            appState.sauvegarderClients();

            showSuccess("✅ Compte créé avec succès ! Vous pouvez maintenant vous connecter.");
            reinitialiserFormulaireInscription();

        } catch (Exception e) {
            showRegMessage("❌ Erreur lors de la création du compte: " + e.getMessage(), true);
        }
    }

    private boolean validerFormulaireInscription() {
        String nom = regNom.getText().trim();
        String prenom = regPrenom.getText().trim();
        String telephone = regTelephone.getText().trim();
        String email = regEmail.getText().trim().toLowerCase();
        String adresse = regAdresse.getText().trim();
        String password = regPassword.getText().trim();
        String confirmPassword = regConfirmPassword.getText().trim();

        // Vérification des champs vides
        if (nom.isEmpty() || prenom.isEmpty() || telephone.isEmpty() ||
                email.isEmpty() || adresse.isEmpty() || password.isEmpty()) {
            showRegMessage("❌ Tous les champs sont obligatoires", true);
            return false;
        }

        // Vérification email
        if (!email.contains("@") || !email.contains(".")) {
            showRegMessage("❌ Format d'email invalide", true);
            regEmail.requestFocus();
            return false;
        }

        // Vérification mot de passe
        if (password.length() < 6) {
            showRegMessage("❌ Le mot de passe doit contenir au moins 6 caractères", true);
            regPassword.requestFocus();
            return false;
        }

        // Vérification confirmation mot de passe
        if (!password.equals(confirmPassword)) {
            showRegMessage("❌ Les mots de passe ne correspondent pas", true);
            regConfirmPassword.clear();
            regConfirmPassword.requestFocus();
            return false;
        }

        // Vérification téléphone
        if (!telephone.matches("\\d{10}")) {
            showRegMessage("❌ Le téléphone doit contenir 10 chiffres", true);
            regTelephone.requestFocus();
            return false;
        }

        return true;
    }

    private boolean emailExisteDeja(String email) {
        return appState.getClients().stream()
                .anyMatch(client -> client.getEmail().equalsIgnoreCase(email));
    }

    private boolean telephoneExisteDeja(String telephone) {
        return appState.getClients().stream()
                .anyMatch(client -> client.getTelephone().equals(telephone));
    }

    private Client creerNouveauClient() {
        String id = "CLIENT_" + System.currentTimeMillis();
        String nom = regNom.getText().trim();
        String prenom = regPrenom.getText().trim();
        String telephone = regTelephone.getText().trim();
        String email = regEmail.getText().trim().toLowerCase();
        String adresse = regAdresse.getText().trim();
        String password = regPassword.getText().trim();

        return new Client(id, nom, prenom, telephone, adresse, email, password);
    }

    private void reinitialiserFormulaireInscription() {
        regNom.clear();
        regPrenom.clear();
        regTelephone.clear();
        regEmail.clear();
        regAdresse.clear();
        regPassword.clear();
        regConfirmPassword.clear();
        regMessage.setVisible(false);
    }

    private void ouvrirGestionBibliotheque() {
        try {
            // Fermer la fenêtre d'authentification
            Stage stageActuel = (Stage) loginEmail.getScene().getWindow();
            stageActuel.close();

            // Ouvrir la gestion bibliothèque
            MainBibliotheque mainBiblio = new MainBibliotheque();
            Stage nouvelleStage = new Stage();
            mainBiblio.start(nouvelleStage);

        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
        }
    }

    // ==================== MÉTHODES UTILITAIRES ====================
    private void showLoginMessage(String message, boolean isError) {
        loginMessage.setText(message);
        loginMessage.setVisible(true);
        loginMessage.setStyle(isError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #2ecc71;");
    }

    private void showRegMessage(String message, boolean isError) {
        regMessage.setText(message);
        regMessage.setVisible(true);
        regMessage.setStyle(isError ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #2ecc71;");
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}