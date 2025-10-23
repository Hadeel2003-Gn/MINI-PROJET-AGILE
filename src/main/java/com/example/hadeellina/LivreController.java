package com.example.hadeellina;

import com.example.tp1gd.models.Livre;
import com.example.tp1gd.utils.CSVImporter;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.*;

public class LivreController {

    private ApplicationState appState = ApplicationState.getInstance();

    // 🔐 ÉLÉMENTS POUR AUTHENTIFICATION
    @FXML private Label lblUtilisateurConnecte;
    @FXML private Button btnLogin;
    @FXML private Button btnLogout;
    @FXML private VBox vboxFormulaire;
    @FXML private HBox hboxBoutonsActions;

    // Champs du formulaire
    @FXML private TextField txtTitreLivre;
    @FXML private TextField txtAuteurLivre;
    @FXML private TextField txtAnneeLivre;
    @FXML private Spinner<Integer> spinnerCopies;
    @FXML private ComboBox<String> comboLangue;
    @FXML private TextField txtISBN;
    @FXML private TextField txtPages;
    @FXML private TextField txtGenre;
    @FXML private TextField txtEditeur;
    @FXML private TextArea txtNouvellesInfos;
    @FXML private TextField txtRecherche;

    // Tableau et colonnes
    @FXML private TableView<Livre> tableLivres;
    @FXML private TableColumn<Livre, String> colTitre;
    @FXML private TableColumn<Livre, String> colAuteur;
    @FXML private TableColumn<Livre, String> colAnnee;
    @FXML private TableColumn<Livre, Integer> colTotalCopies;
    @FXML private TableColumn<Livre, Integer> colCopiesDisponibles;
    @FXML private TableColumn<Livre, Integer> colCopiesEmpruntees;
    @FXML private TableColumn<Livre, String> colLangue;
    @FXML private TableColumn<Livre, String> colISBN;
    @FXML private TableColumn<Livre, String> colPages;
    @FXML private TableColumn<Livre, String> colGenre;
    @FXML private TableColumn<Livre, String> colEditeur;

    // Labels d'information
    @FXML private Label lblTotalLivres;
    @FXML private Label lblLivresDisponibles;
    @FXML private Label lblLivresEmpruntes;
    @FXML private Label lblSelection;

    private Map<String, TableColumn<Livre, String>> colonnesDynamiques = new HashMap<>();
    private ObservableList<Livre> livres = FXCollections.observableArrayList();
    private ObservableList<Livre> livresFiltres = FXCollections.observableArrayList();
    private Livre livreSelectionne = null;

    @FXML
    private void initialize() {
        configurerColonnes();
        configurerSpinnerEtCombo();
        configurerSelection();
        configurerDoubleClick();
        configurerRecherche();
        chargerEtImporterAutomatiquement();

        // 🔐 INITIALISER L'ÉTAT DE CONNEXION
        mettreAJourEtatConnexion();
    }

    // 🔐 MÉTHODE POUR GÉRER LE LOGIN
    @FXML
    private void onLogin(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("🔐 Connexion Administrateur");
        dialog.setHeaderText("Veuillez entrer vos identifiants");

        // Création du formulaire de login
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Nom d'utilisateur");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Mot de passe");

        grid.add(new Label("Utilisateur:"), 0, 0);
        grid.add(txtUsername, 1, 0);
        grid.add(new Label("Mot de passe:"), 0, 1);
        grid.add(txtPassword, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType btnConnexion = new ButtonType("Se connecter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnConnexion, ButtonType.CANCEL);

        // Focus sur le champ username
        javafx.application.Platform.runLater(() -> txtUsername.requestFocus());

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == btnConnexion) {
            String username = txtUsername.getText().trim();
            String password = txtPassword.getText().trim();

            if (verifierCredentials(username, password)) {
                appState.setAdminConnecte(true);
                appState.setNomAdmin(username);
                mettreAJourEtatConnexion();
                showSuccess("✅ Bienvenue " + username + " !");
            } else {
                showError("❌ Identifiants incorrects !\n\nUtilisez:\nUsername: admin\nPassword: admin123");
            }
        }
    }

    // 🔐 MÉTHODE POUR GÉRER LE LOGOUT
    @FXML
    private void onLogout(ActionEvent event) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Déconnexion");
        confirmation.setHeaderText("Êtes-vous sûr de vouloir vous déconnecter ?");
        confirmation.setContentText("Vous devrez vous reconnecter pour modifier les livres.");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            appState.setAdminConnecte(false);
            appState.setNomAdmin(null);
            mettreAJourEtatConnexion();
            reinitialiserChamps();
            showInfo("👋 Déconnexion réussie !");
        }
    }

    // 🔐 VÉRIFICATION DES CREDENTIALS
    private boolean verifierCredentials(String username, String password) {
        // Version simple - À améliorer avec une vraie base de données
        return (username.equals("admin") && password.equals("admin123")) ||
                (username.equals("bibliothecaire") && password.equals("biblio2024"));
    }

    // 🔐 MISE À JOUR DE L'INTERFACE SELON L'ÉTAT DE CONNEXION
    private void mettreAJourEtatConnexion() {
        if (appState.isAdminConnecte()) {
            // Mode connecté
            lblUtilisateurConnecte.setText("👤 Connecté: " + appState.getNomAdmin());
            lblUtilisateurConnecte.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
            btnLogin.setVisible(false);
            btnLogin.setManaged(false);
            btnLogout.setVisible(true);
            btnLogout.setManaged(true);

            // Activer les fonctionnalités d'édition
            activerModeEdition(true);
        } else {
            // Mode déconnecté
            lblUtilisateurConnecte.setText("👤 Non connecté - Mode Lecture Seule");
            lblUtilisateurConnecte.setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
            btnLogin.setVisible(true);
            btnLogin.setManaged(true);
            btnLogout.setVisible(false);
            btnLogout.setManaged(false);

            // Désactiver les fonctionnalités d'édition
            activerModeEdition(false);
        }
    }

    // 🔐 ACTIVER/DÉSACTIVER LE MODE ÉDITION
    private void activerModeEdition(boolean activer) {
        // Désactiver le formulaire
        if (vboxFormulaire != null) {
            vboxFormulaire.setDisable(!activer);
        }

        // Désactiver les boutons d'action
        if (hboxBoutonsActions != null) {
            hboxBoutonsActions.setDisable(!activer);
        }

        // Si les éléments ne sont pas dans des conteneurs, les désactiver individuellement
        if (txtTitreLivre != null) txtTitreLivre.setDisable(!activer);
        if (txtAuteurLivre != null) txtAuteurLivre.setDisable(!activer);
        if (txtAnneeLivre != null) txtAnneeLivre.setDisable(!activer);
        if (spinnerCopies != null) spinnerCopies.setDisable(!activer);
        if (comboLangue != null) comboLangue.setDisable(!activer);
        if (txtISBN != null) txtISBN.setDisable(!activer);
        if (txtPages != null) txtPages.setDisable(!activer);
        if (txtGenre != null) txtGenre.setDisable(!activer);
        if (txtEditeur != null) txtEditeur.setDisable(!activer);
        if (txtNouvellesInfos != null) txtNouvellesInfos.setDisable(!activer);
    }

    // 🔐 VÉRIFIER AVANT CHAQUE ACTION
    private boolean verifierAccesAdmin() {
        if (!appState.isAdminConnecte()) {
            showWarning("🔒 Accès refusé !\n\nVous devez être connecté en tant qu'administrateur pour effectuer cette action.\n\nCliquez sur 'Se connecter' pour vous authentifier.");
            return false;
        }
        return true;
    }

    private void configurerColonnes() {
        colTitre.setCellValueFactory(data -> data.getValue().titreProperty());
        colAuteur.setCellValueFactory(data -> data.getValue().auteurProperty());
        colAnnee.setCellValueFactory(data -> data.getValue().anneeProperty());
        colTotalCopies.setCellValueFactory(data -> data.getValue().nombreCopiesProperty().asObject());
        colCopiesDisponibles.setCellValueFactory(data -> data.getValue().copiesDisponiblesProperty().asObject());
        colCopiesEmpruntees.setCellValueFactory(data -> {
            int empruntees = data.getValue().getCopiesEmpruntees();
            return new SimpleIntegerProperty(empruntees).asObject();
        });
        colLangue.setCellValueFactory(data -> data.getValue().langueProperty());

        colISBN.setCellValueFactory(data -> {
            String isbn = data.getValue().getInformationPersonnalisee("ISBN");
            return new javafx.beans.property.SimpleStringProperty(isbn != null ? isbn : "");
        });

        colPages.setCellValueFactory(data -> {
            String pages = data.getValue().getInformationPersonnalisee("Pages");
            return new javafx.beans.property.SimpleStringProperty(pages != null ? pages : "");
        });

        colGenre.setCellValueFactory(data -> {
            String genre = data.getValue().getInformationPersonnalisee("Genre");
            return new javafx.beans.property.SimpleStringProperty(genre != null ? genre : "");
        });

        colEditeur.setCellValueFactory(data -> {
            String editeur = data.getValue().getInformationPersonnalisee("Editeur");
            return new javafx.beans.property.SimpleStringProperty(editeur != null ? editeur : "");
        });

        tableLivres.setRowFactory(tv -> {
            TableRow<Livre> row = new TableRow<Livre>() {
                @Override
                protected void updateItem(Livre livre, boolean empty) {
                    super.updateItem(livre, empty);

                    if (empty || livre == null) {
                        setStyle("");
                        setTooltip(null);
                    } else {
                        boolean estEmprunte = appState.getTousEmprunts().stream()
                                .anyMatch(e -> e.getIdLivre().equals(livre.getId()));

                        if (estEmprunte || !livre.isDisponible()) {
                            setStyle("-fx-background-color: #FFEBEE; -fx-text-fill: #C62828;");
                            setTooltip(new Tooltip("🔴 Ce livre est actuellement emprunté"));
                        } else {
                            setStyle("-fx-background-color: #E8F5E8; -fx-text-fill: #2E7D32;");
                            setTooltip(new Tooltip("🟢 Livre disponible"));
                        }
                    }
                }
            };

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Livre livre = row.getItem();
                    if (livre != null) {
                        if (appState.isAdminConnecte()) {
                            remplirChampsAvecLivre(livre);
                            showInfo("Livre chargé pour modification : " + livre.getTitre());
                        } else {
                            showInfo("📖 " + livre.getTitre() + " par " + livre.getAuteur() +
                                    "\n\n🔒 Connectez-vous pour modifier ce livre.");
                        }
                    }
                }
            });

            return row;
        });
    }

    private void configurerSpinnerEtCombo() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        spinnerCopies.setValueFactory(valueFactory);

        comboLangue.getItems().addAll("Français", "Anglais", "Espagnol", "Allemand", "Italien", "Arabe", "Autre");
        comboLangue.setValue("Français");
    }

    private void configurerSelection() {
        tableLivres.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                livreSelectionne = newVal;
                lblSelection.setText("📖 Sélection: " + newVal.getTitre() + " par " + newVal.getAuteur());
                lblSelection.setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold;");

                boolean estEmprunte = appState.getTousEmprunts().stream()
                        .anyMatch(e -> e.getIdLivre().equals(newVal.getId()));
                if (estEmprunte) {
                    lblSelection.setText(lblSelection.getText() + " 🔴 EMPRUNTÉ");
                }
            } else {
                livreSelectionne = null;
                lblSelection.setText("Aucune sélection");
                lblSelection.setStyle("-fx-text-fill: gray;");
            }
        });
    }

    private void configurerDoubleClick() {
        tableLivres.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Livre livre = tableLivres.getSelectionModel().getSelectedItem();
                if (livre != null && appState.isAdminConnecte()) {
                    remplirChampsAvecLivre(livre);
                    boolean estEmprunte = appState.getTousEmprunts().stream()
                            .anyMatch(e -> e.getIdLivre().equals(livre.getId()));
                    String statut = estEmprunte ? " (🔴 EMPRUNTÉ)" : " (🟢 DISPONIBLE)";
                    showInfo("Livre chargé pour modification : " + livre.getTitre() + statut);
                }
            }
        });
    }

    private void configurerRecherche() {
        txtRecherche.textProperty().addListener((obs, oldVal, newVal) -> {
            filtrerLivres(newVal);
        });
    }

    private void filtrerLivres(String recherche) {
        if (recherche == null || recherche.trim().isEmpty()) {
            livresFiltres.setAll(livres);
        } else {
            String rechercheMinuscule = recherche.toLowerCase().trim();
            livresFiltres.setAll(livres.stream()
                    .filter(livre ->
                            livre.getTitre().toLowerCase().contains(rechercheMinuscule) ||
                                    livre.getAuteur().toLowerCase().contains(rechercheMinuscule) ||
                                    livre.getAnnee().contains(rechercheMinuscule) ||
                                    livre.getLangue().toLowerCase().contains(rechercheMinuscule) ||
                                    livre.getInformationsPersonnalisees().values().stream()
                                            .anyMatch(val -> val != null && val.toLowerCase().contains(rechercheMinuscule))
                    )
                    .toList()
            );
        }
        tableLivres.setItems(livresFiltres);
    }

    private void remplirChampsAvecLivre(Livre livre) {
        txtTitreLivre.setText(livre.getTitre());
        txtAuteurLivre.setText(livre.getAuteur());
        txtAnneeLivre.setText(livre.getAnnee());
        spinnerCopies.getValueFactory().setValue(livre.getNombreCopies());
        comboLangue.setValue(livre.getLangue());

        txtISBN.setText(livre.getInformationPersonnalisee("ISBN"));
        txtPages.setText(livre.getInformationPersonnalisee("Pages"));
        txtGenre.setText(livre.getInformationPersonnalisee("Genre"));
        txtEditeur.setText(livre.getInformationPersonnalisee("Editeur"));

        StringBuilder nouvellesInfos = new StringBuilder();
        livre.getInformationsPersonnalisees().forEach((cle, valeur) -> {
            if (valeur != null && !valeur.equals("null") && !cle.isEmpty() &&
                    !estInformationPrincipale(cle)) {
                nouvellesInfos.append(cle).append(": ").append(valeur).append("\n");
            }
        });
        txtNouvellesInfos.setText(nouvellesInfos.toString());

        livreSelectionne = livre;
    }

    private boolean estInformationPrincipale(String cle) {
        return cle.equals("ISBN") || cle.equals("Pages") || cle.equals("Genre") ||
                cle.equals("Editeur") || cle.equals("Source") || cle.equals("Langue");
    }

    private void chargerEtImporterAutomatiquement() {
        try {
            appState.rafraichirDonnees();
            List<Livre> livresExistants = appState.getLivres();

            if (livresExistants.isEmpty() || livresExistants.size() < 10) {
                importerDepuisCSV();
            } else {
                livres.setAll(livresExistants);
                livresFiltres.setAll(livres);
                tableLivres.setItems(livresFiltres);
                creerColonnesDynamiques();
                mettreAJourStatistiques();
                tableLivres.refresh();
            }

        } catch (Exception e) {
            importerDepuisCSV();
        }
    }

    private void importerDepuisCSV() {
        try {
            String csvPath = "books.csv";
            File csvFile = new File(csvPath);

            if (!csvFile.exists()) {
                genererDonneesTest();
                return;
            }

            List<Livre> nouveauxLivres = CSVImporter.importerLivresDepuisCSV(csvPath);

            if (!nouveauxLivres.isEmpty()) {
                appState.getLivres().clear();
                appState.getLivres().addAll(nouveauxLivres);
                appState.sauvegarderLivres();

                livres.setAll(appState.getLivres());
                livresFiltres.setAll(livres);
                tableLivres.setItems(livresFiltres);

                creerColonnesDynamiques();
                mettreAJourStatistiques();
                tableLivres.refresh();

                showSuccess(nouveauxLivres.size() + " livres importés avec succès !");
            } else {
                genererDonneesTest();
            }

        } catch (Exception e) {
            showError("Erreur lors de l'importation : " + e.getMessage());
            genererDonneesTest();
        }
    }

    private void genererDonneesTest() {
        try {
            List<Livre> livresTest = CSVImporter.genererLivresTest();
            appState.getLivres().clear();
            appState.getLivres().addAll(livresTest);
            appState.sauvegarderLivres();

            livres.setAll(appState.getLivres());
            livresFiltres.setAll(livres);
            tableLivres.setItems(livresFiltres);

            creerColonnesDynamiques();
            mettreAJourStatistiques();
            tableLivres.refresh();

            showInfo("🎲 " + livresTest.size() + " livres de test générés");

        } catch (Exception e) {
            showError("Erreur critique : " + e.getMessage());
        }
    }

    private void creerColonnesDynamiques() {
        Set<String> toutesLesCles = new HashSet<>();
        for (Livre livre : livres) {
            toutesLesCles.addAll(livre.getInformationsPersonnalisees().keySet());
        }

        Set<String> clesExclues = new HashSet<>(Arrays.asList(
                "ISBN", "Pages", "Genre", "Editeur", "Source", "Langue"
        ));
        toutesLesCles.removeAll(clesExclues);

        for (String cle : toutesLesCles) {
            if (!colonnesDynamiques.containsKey(cle) && !cle.trim().isEmpty()) {
                TableColumn<Livre, String> colonne = new TableColumn<>(formatCleColonne(cle));
                colonne.setPrefWidth(120);

                colonne.setCellValueFactory(cellData -> {
                    String valeur = cellData.getValue().getInformationPersonnalisee(cle);
                    return new javafx.beans.property.SimpleStringProperty(valeur != null ? valeur : "");
                });

                tableLivres.getColumns().add(colonne);
                colonnesDynamiques.put(cle, colonne);
            }
        }
    }

    private String formatCleColonne(String cle) {
        String formatted = cle.replaceAll("([A-Z])", " $1").trim();
        return formatted.substring(0, 1).toUpperCase() + formatted.substring(1);
    }

    // 🔥 MÉTHODE AMÉLIORÉE POUR METTRE À JOUR LES STATISTIQUES
    private void mettreAJourStatistiques() {
        ApplicationState appState = ApplicationState.getInstance();

        // 🔥 CORRECTION : Utiliser les statistiques globales cohérentes
        Map<String, Integer> stats = appState.getStatistiquesGlobales();

        int total = stats.get("total");
        int disponibles = stats.get("disponibles");
        int empruntes = stats.get("empruntes");

        lblTotalLivres.setText("📚 Total: " + total);
        lblLivresDisponibles.setText("🟢 Disponibles: " + disponibles);
        lblLivresEmpruntes.setText("🔴 Empruntés: " + empruntes);

        System.out.println("📊 Statistiques Livres - Total: " + total +
                ", Disponibles: " + disponibles +
                ", Empruntés: " + empruntes);
    }

    @FXML
    private void onAjouterLivre(ActionEvent event) {
        if (!verifierAccesAdmin()) return;

        String titre = txtTitreLivre.getText().trim();
        String auteur = txtAuteurLivre.getText().trim();
        String annee = txtAnneeLivre.getText().trim();

        if (titre.isEmpty() || auteur.isEmpty() || annee.isEmpty()) {
            showWarning("Veuillez remplir les champs obligatoires (Titre, Auteur, Année) !");
            return;
        }

        try {
            String isbn = txtISBN.getText().trim();
            if (!isbn.isEmpty() && livreExisteParISBN(isbn)) {
                showWarning("⚠️ Ce livre existe déjà (ISBN: " + isbn + ") !");
                return;
            }

            if (livreExisteParTitreAuteur(titre, auteur)) {
                showWarning("⚠️ Ce livre existe déjà !\nTitre: " + titre + "\nAuteur: " + auteur);
                return;
            }

            String id = "L_" + System.currentTimeMillis();
            int nombreCopies = spinnerCopies.getValue();

            Livre livre = new Livre(id, titre, auteur, annee, nombreCopies);
            livre.setLangue(comboLangue.getValue());

            ajouterInformationsPrincipales(livre);
            ajouterNouvellesInformations(livre);

            appState.getLivres().add(livre);
            appState.sauvegarderLivres();

            livres.setAll(appState.getLivres());
            livresFiltres.setAll(livres);
            tableLivres.setItems(livresFiltres);

            mettreAJourColonnesDynamiques();
            mettreAJourStatistiques();
            reinitialiserChamps();

            showSuccess("✅ Livre '" + titre + "' ajouté avec succès !");

        } catch (Exception e) {
            showError("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    private boolean livreExisteParISBN(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) return false;
        String isbnNormalise = isbn.trim().toLowerCase();
        return appState.getLivres().stream()
                .anyMatch(livre -> {
                    String livreISBN = livre.getInformationPersonnalisee("ISBN");
                    return livreISBN != null && livreISBN.trim().toLowerCase().equals(isbnNormalise);
                });
    }

    private boolean livreExisteParTitreAuteur(String titre, String auteur) {
        String titreNormalise = normaliserTexte(titre);
        String auteurNormalise = normaliserTexte(auteur);
        return appState.getLivres().stream()
                .anyMatch(livre ->
                        normaliserTexte(livre.getTitre()).equals(titreNormalise) &&
                                normaliserTexte(livre.getAuteur()).equals(auteurNormalise)
                );
    }

    private String normaliserTexte(String texte) {
        if (texte == null) return "";
        return texte.trim().toLowerCase().replaceAll("\\s+", " ");
    }

    @FXML
    private void onModifierLivre(ActionEvent event) {
        if (!verifierAccesAdmin()) return;

        Livre livre = tableLivres.getSelectionModel().getSelectedItem();
        if (livre == null) {
            showWarning("⚠️ Veuillez sélectionner un livre dans le tableau !");
            return;
        }
        remplirChampsAvecLivre(livre);
        showInfo("📝 Livre chargé. Modifiez les champs puis cliquez sur 'Enregistrer'");
    }

    @FXML
    private void onSauvegarderLivre(ActionEvent event) {
        if (!verifierAccesAdmin()) return;

        if (livreSelectionne == null) {
            showWarning("⚠️ Veuillez d'abord sélectionner un livre à modifier !");
            return;
        }

        String titre = txtTitreLivre.getText().trim();
        String auteur = txtAuteurLivre.getText().trim();
        String annee = txtAnneeLivre.getText().trim();

        if (titre.isEmpty() || auteur.isEmpty() || annee.isEmpty()) {
            showWarning("⚠️ Veuillez remplir tous les champs obligatoires !");
            return;
        }

        try {
            int nombreCopies = spinnerCopies.getValue();
            int difference = nombreCopies - livreSelectionne.getNombreCopies();

            Livre livreModifie = new Livre(livreSelectionne.getId(), titre, auteur, annee, nombreCopies);
            livreModifie.setCopiesDisponibles(Math.max(0, livreSelectionne.getCopiesDisponibles() + difference));
            livreModifie.setLangue(comboLangue.getValue());

            ajouterInformationsPrincipales(livreModifie);
            ajouterNouvellesInformations(livreModifie);

            int index = appState.getLivres().indexOf(livreSelectionne);
            appState.getLivres().set(index, livreModifie);
            appState.sauvegarderLivres();

            livres.setAll(appState.getLivres());
            livresFiltres.setAll(livres);
            tableLivres.setItems(livresFiltres);

            mettreAJourColonnesDynamiques();
            mettreAJourStatistiques();
            reinitialiserChamps();
            livreSelectionne = null;

            showSuccess("✅ Livre '" + titre + "' modifié avec succès !");

        } catch (Exception e) {
            showError("❌ Erreur lors de la modification : " + e.getMessage());
        }
    }

    @FXML
    private void onSupprimerLivre(ActionEvent event) {
        if (!verifierAccesAdmin()) return;

        Livre livre = tableLivres.getSelectionModel().getSelectedItem();
        if (livre == null) {
            showWarning("⚠️ Veuillez sélectionner un livre à supprimer !");
            return;
        }

        boolean estEmprunte = appState.getTousEmprunts().stream()
                .anyMatch(e -> e.getIdLivre().equals(livre.getId()));

        if (estEmprunte) {
            showWarning("❌ Impossible de supprimer ce livre !\nIl est actuellement emprunté.\nVeuillez d'abord retourner le livre avant de le supprimer.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Êtes-vous sûr de vouloir supprimer ce livre ?");
        confirmation.setContentText("Livre : " + livre.getTitre() + "\nAuteur : " + livre.getAuteur());

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                appState.getLivres().remove(livre);
                appState.sauvegarderLivres();

                livres.setAll(appState.getLivres());
                livresFiltres.setAll(livres);
                tableLivres.setItems(livresFiltres);

                mettreAJourColonnesDynamiques();
                mettreAJourStatistiques();
                reinitialiserChamps();
                showSuccess("✅ Livre supprimé avec succès !");

            } catch (Exception e) {
                showError("❌ Erreur : " + e.getMessage());
            }
        }
    }

    @FXML
    private void onReinitialiser(ActionEvent event) {
        reinitialiserChamps();
        livreSelectionne = null;
        tableLivres.getSelectionModel().clearSelection();
        showInfo("🔄 Formulaire réinitialisé");
    }

    @FXML
    private void onActualiser(ActionEvent event) {
        try {
            appState.rafraichirDonnees();
            livres.setAll(appState.getLivres());
            livresFiltres.setAll(livres);
            tableLivres.setItems(livresFiltres);
            mettreAJourColonnesDynamiques();
            mettreAJourStatistiques();
            tableLivres.refresh();
            showSuccess("🔄 Données actualisées !");
        } catch (Exception e) {
            showError("Erreur : " + e.getMessage());
        }
    }

    // 🔥 SUPPRESSION : La méthode onReimporterCSV a été supprimée

    private void ajouterInformationsPrincipales(Livre livre) {
        String isbn = txtISBN.getText().trim();
        if (!isbn.isEmpty()) livre.ajouterInformationPersonnalisee("ISBN", isbn);

        String pages = txtPages.getText().trim();
        if (!pages.isEmpty()) livre.ajouterInformationPersonnalisee("Pages", pages);

        String genre = txtGenre.getText().trim();
        if (!genre.isEmpty()) livre.ajouterInformationPersonnalisee("Genre", genre);

        String editeur = txtEditeur.getText().trim();
        if (!editeur.isEmpty()) livre.ajouterInformationPersonnalisee("Editeur", editeur);
    }

    private void ajouterNouvellesInformations(Livre livre) {
        String infosText = txtNouvellesInfos.getText().trim();
        if (!infosText.isEmpty()) {
            String[] lignes = infosText.split("\n");
            for (String ligne : lignes) {
                ligne = ligne.trim();
                if (!ligne.isEmpty() && ligne.contains(":")) {
                    String[] parties = ligne.split(":", 2);
                    if (parties.length == 2) {
                        String cle = parties[0].trim().replaceAll("[^a-zA-Z0-9_\\s]", "").replaceAll("\\s+", "");
                        String valeur = parties[1].trim();

                        if (!cle.isEmpty() && !valeur.isEmpty() && !estInformationPrincipale(cle)) {
                            livre.ajouterInformationPersonnalisee(cle, valeur);
                        }
                    }
                }
            }
        }
    }

    private void mettreAJourColonnesDynamiques() {
        tableLivres.getColumns().removeAll(colonnesDynamiques.values());
        colonnesDynamiques.clear();
        creerColonnesDynamiques();
        tableLivres.refresh();
    }

    private void reinitialiserChamps() {
        txtTitreLivre.clear();
        txtAuteurLivre.clear();
        txtAnneeLivre.clear();
        spinnerCopies.getValueFactory().setValue(1);
        comboLangue.setValue("Français");
        txtISBN.clear();
        txtPages.clear();
        txtGenre.clear();
        txtEditeur.clear();
        txtNouvellesInfos.clear();
        txtRecherche.clear();
        livreSelectionne = null;
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
        alert.setHeaderText("Une erreur s'est produite");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}