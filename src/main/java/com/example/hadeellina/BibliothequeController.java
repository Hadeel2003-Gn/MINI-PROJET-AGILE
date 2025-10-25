package com.example.hadeellina;

import com.example.hadeellina.models.Client;
import com.example.hadeellina.models.Livre;
import com.example.hadeellina.models.Emprunt;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class BibliothequeController {

    private ApplicationState appState = ApplicationState.getInstance();

    // Champs du formulaire
    @FXML private TextField txtClientConnecte;
    @FXML private ComboBox<Livre> comboLivres;
    @FXML private DatePicker datePicker;

    @FXML private TextField txtRechercheLivre;

    // Filtres avancés
    @FXML private ComboBox<String> comboFiltreGenre;
    @FXML private ComboBox<String> comboFiltreLangue;
    @FXML private CheckBox checkDisponiblesSeulement;

    // Tableau des EMPRUNTS
    @FXML private TableView<EmpruntGlobal> tableGlobal;
    @FXML private TableColumn<EmpruntGlobal, String> colTitreLivre;
    @FXML private TableColumn<EmpruntGlobal, String> colAuteur;
    @FXML private TableColumn<EmpruntGlobal, String> colNomClient;
    @FXML private TableColumn<EmpruntGlobal, String> colTelClient;
    @FXML private TableColumn<EmpruntGlobal, String> colDate;
    @FXML private TableColumn<EmpruntGlobal, String> colDateRetour;
    @FXML private TableColumn<EmpruntGlobal, String> colStatut;

    // Tableau des LIVRES
    @FXML private TableView<Livre> tableLivres;
    @FXML private TableColumn<Livre, String> colTitre;
    @FXML private TableColumn<Livre, String> colAuteurLivre;
    @FXML private TableColumn<Livre, String> colAnnee;
    @FXML private TableColumn<Livre, Integer> colTotalCopies;
    @FXML private TableColumn<Livre, Integer> colCopiesDisponibles;
    @FXML private TableColumn<Livre, Integer> colCopiesEmpruntees;
    @FXML private TableColumn<Livre, String> colLangue;
    @FXML private TableColumn<Livre, String> colISBN;
    @FXML private TableColumn<Livre, String> colPages;
    @FXML private TableColumn<Livre, String> colGenre;
    @FXML private TableColumn<Livre, String> colEditeur;

    // Labels statistiques
    @FXML private Label lblTotalLivres;
    @FXML private Label lblLivresDisponibles;
    @FXML private Label lblEmpruntsActifs;
    @FXML private Label lblClients;

    // 🔥 LABELS POUR STATISTIQUES PERSONNELLES
    @FXML private Label lblEmpruntsTotal;
    @FXML private Label lblEmpruntsEnCours;
    @FXML private Label lblEmpruntsRetard;

    // 🔐 BOUTON DÉCONNEXION
    @FXML private Button btnDeconnexion;

    private ObservableList<Livre> livresObs = FXCollections.observableArrayList();
    private ObservableList<Livre> livresFiltres = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurerColonnesEmprunts();
        configurerColonnesLivres();
        configurerFiltres();
        chargerDonneesInitiales();
        afficherClientConnecte();
        configurerBoutonDeconnexion();

        datePicker.setValue(LocalDate.now());
    }

    // 🔐 CONFIGURER LE BOUTON DÉCONNEXION
    private void configurerBoutonDeconnexion() {
        if (btnDeconnexion != null) {
            btnDeconnexion.setVisible(appState.isClientConnecte());
            btnDeconnexion.setTooltip(new Tooltip("Se déconnecter et retourner à la page de connexion"));
        }
    }

    // 🔐 AFFICHER LE CLIENT CONNECTÉ AVEC SES STATISTIQUES PERSONNELLES
    private void afficherClientConnecte() {
        if (appState.isClientConnecte() && appState.getClientActuel() != null) {
            Client client = appState.getClientActuel();

            // Mettre à jour les statistiques
            mettreAJourStatistiquesPersonnelles();

            if (btnDeconnexion != null) {
                btnDeconnexion.setVisible(true);
            }

            // Message de bienvenue avec statistiques
            List<Emprunt> empruntsClient = appState.getEmpruntsClientActuel();
            int empruntsTotal = empruntsClient.size();
            int empruntsEnRetard = (int) empruntsClient.stream()
                    .filter(Emprunt::estEnRetard)
                    .count();

            String messageBienvenue = String.format(
                    "👋 Bienvenue %s !\n\n" +
                            "📊 Vos statistiques :\n" +
                            "• Emprunts totaux : %d\n" +
                            "• Emprunts en cours : %d\n" +
                            "• Emprunts en retard : %d\n\n" +
                            "Cliquez sur '🚪 Déconnexion' pour retourner à la page de connexion.",
                    client.getPrenom(), empruntsTotal, empruntsTotal, empruntsEnRetard
            );
            showAlert(messageBienvenue);

        } else {
            if (btnDeconnexion != null) {
                btnDeconnexion.setVisible(false);
            }

            if (txtClientConnecte != null) {
                txtClientConnecte.setText("");
                txtClientConnecte.setPromptText("Aucun client connecté");
                txtClientConnecte.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575; " +
                        "-fx-background-color: #F5F5F5; -fx-border-color: #BDBDBD; " +
                        "-fx-border-width: 1px; -fx-border-radius: 5px; " +
                        "-fx-padding: 8px 12px; -fx-background-radius: 5px;");
            }

            // Réinitialiser les statistiques
            mettreAJourStatistiquesPersonnelles();
        }
    }

    // 🔐 MÉTHODE POUR DÉCONNEXION COMPLÈTE
    @FXML
    private void onDeconnexion(ActionEvent event) {
        try {
            if (appState.isClientConnecte()) {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Déconnexion");
                confirmation.setHeaderText("Êtes-vous sûr de vouloir vous déconnecter ?");
                confirmation.setContentText("Vous serez redirigé vers la page de connexion.");

                if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    appState.sauvegarderLivres();
                    appState.sauvegarderClients();
                    appState.sauvegarderEmprunts();

                    String nomClient = appState.getClientActuel().getNomComplet();
                    appState.deconnecterClient();

                    afficherClientConnecte();

                    showAlert("👋 À bientôt " + nomClient + " ! Déconnexion réussie.");

                    Stage stageActuel = (Stage) btnDeconnexion.getScene().getWindow();
                    stageActuel.close();

                    ouvrirPageAuthentification();
                }
            } else {
                showAlert("🔒 Aucun client n'est actuellement connecté.");
            }
        } catch (Exception e) {
            showAlert("❌ Erreur lors de la déconnexion : " + e.getMessage());
        }
    }

    // 🔐 MÉTHODE POUR OUVRIR LA PAGE D'AUTHENTIFICATION
    private void ouvrirPageAuthentification() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tp1gd/auth-client.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load(), 600, 600));
            stage.setTitle("📚 Bibliothèque - Espace Client");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            showAlert("❌ Erreur : Impossible d'ouvrir la page de connexion");
            e.printStackTrace();
        }
    }

    private void configurerColonnesEmprunts() {
        colTitreLivre.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("titreLivre"));
        colAuteur.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("auteur"));
        colNomClient.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nomClient"));
        colTelClient.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("telephoneClient"));
        colDate.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("date"));
        colDateRetour.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("dateRetour"));
        colStatut.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("statut"));

        colNomClient.setCellFactory(column -> new TableCell<EmpruntGlobal, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #2E7D32;");
                }
            }
        });
    }

    private void configurerColonnesLivres() {
        colTitre.setCellValueFactory(data -> data.getValue().titreProperty());
        colAuteurLivre.setCellValueFactory(data -> data.getValue().auteurProperty());
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
                        final Livre livreFinal = livre;
                        boolean estEmprunte = appState.getTousEmprunts().stream()
                                .anyMatch(e -> e.getIdLivre().equals(livreFinal.getId()));

                        if (estEmprunte || !livreFinal.isDisponible()) {
                            setStyle("-fx-background-color: #FFEBEE; -fx-text-fill: #C62828;");
                            setTooltip(new Tooltip("🔴 Ce livre est actuellement emprunté"));
                        } else {
                            setStyle("-fx-background-color: #E8F5E8; -fx-text-fill: #2E7D32;");
                            setTooltip(new Tooltip("🟢 Livre disponible"));
                        }
                    }
                }
            };

            return row;
        });

        // 🔥 RECHERCHE SIMPLE
        txtRechercheLivre.textProperty().addListener((obs, oldVal, newVal) -> {
            filtrerLivres(newVal);
        });
    }

    private void configurerFiltres() {
        checkDisponiblesSeulement.selectedProperty().addListener((obs, oldVal, newVal) -> {
            appliquerFiltres();
        });

        if (comboFiltreGenre != null) {
            comboFiltreGenre.valueProperty().addListener((obs, oldVal, newVal) -> {
                appliquerFiltres();
            });
        }

        if (comboFiltreLangue != null) {
            comboFiltreLangue.valueProperty().addListener((obs, oldVal, newVal) -> {
                appliquerFiltres();
            });
        }
    }

    private void chargerDonneesInitiales() {
        try {
            appState.rafraichirDonnees();

            livresObs.setAll(appState.getLivres());
            livresFiltres.setAll(livresObs);
            tableLivres.setItems(livresFiltres);

            peuplerFiltres();
            appliquerFiltres();
            chargerEmpruntsEnCours();
            mettreAJourStatistiques();
            mettreAJourStatistiquesPersonnelles();

        } catch (Exception e) {
            showAlert("Erreur lors du chargement : " + e.getMessage());
        }
    }

    private void peuplerFiltres() {
        List<String> genres = appState.getLivres().stream()
                .map(l -> l.getInformationPersonnalisee("Genre"))
                .filter(genre -> genre != null && !genre.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        if (comboFiltreGenre != null) {
            comboFiltreGenre.getItems().clear();
            comboFiltreGenre.getItems().addAll(genres);
        }

        List<String> langues = appState.getLivres().stream()
                .map(Livre::getLangue)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        if (comboFiltreLangue != null) {
            comboFiltreLangue.getItems().clear();
            comboFiltreLangue.getItems().addAll(langues);
        }
    }

    private void appliquerFiltres() {
        final String genreFiltre = comboFiltreGenre != null ? comboFiltreGenre.getValue() : null;
        final String langueFiltre = comboFiltreLangue != null ? comboFiltreLangue.getValue() : null;
        final boolean seulementDisponibles = checkDisponiblesSeulement.isSelected();

        List<Livre> livresFiltres = appState.getLivres().stream()
                .filter(livre -> {
                    boolean filtre = true;

                    if (seulementDisponibles) {
                        filtre = filtre && livre.isDisponible();
                    }

                    if (genreFiltre != null && !genreFiltre.isEmpty()) {
                        String genreLivre = livre.getInformationPersonnalisee("Genre");
                        filtre = filtre && genreFiltre.equals(genreLivre);
                    }

                    if (langueFiltre != null && !langueFiltre.isEmpty()) {
                        filtre = filtre && langueFiltre.equals(livre.getLangue());
                    }

                    return filtre;
                })
                .collect(Collectors.toList());

        comboLivres.getItems().setAll(livresFiltres);
    }

    private void filtrerLivres(String recherche) {
        if (recherche == null || recherche.trim().isEmpty()) {
            livresFiltres.setAll(livresObs);
        } else {
            final String rechercheFinal = recherche.toLowerCase().trim();
            livresFiltres.setAll(livresObs.stream()
                    .filter(livre ->
                            livre.getTitre().toLowerCase().contains(rechercheFinal) ||
                                    livre.getAuteur().toLowerCase().contains(rechercheFinal) ||
                                    livre.getAnnee().contains(rechercheFinal) ||
                                    livre.getLangue().toLowerCase().contains(rechercheFinal) ||
                                    livre.getInformationsPersonnalisees().values().stream()
                                            .anyMatch(val -> val != null && val.toLowerCase().contains(rechercheFinal))
                    )
                    .collect(Collectors.toList())
            );
        }
        tableLivres.setItems(livresFiltres);
    }

    // 🔥 CHARGER LES EMPRUNTS AVEC STATISTIQUES PERSONNELLES
    private void chargerEmpruntsEnCours() {
        ObservableList<EmpruntGlobal> empruntsObs = FXCollections.observableArrayList();

        List<Emprunt> empruntsAAfficher;

        if (appState.isAdminConnecte()) {
            empruntsAAfficher = appState.getTousEmprunts();
        } else if (appState.isClientConnecte()) {
            empruntsAAfficher = appState.getEmpruntsClientActuel();

            // 🔥 METTRE À JOUR LES STATISTIQUES PERSONNELLES
            mettreAJourStatistiquesPersonnelles();

        } else {
            empruntsAAfficher = new ArrayList<>();
        }

        for (Emprunt e : empruntsAAfficher) {
            Livre livre = appState.getLivres().stream()
                    .filter(l -> l.getId().equals(e.getIdLivre()))
                    .findFirst()
                    .orElse(null);

            Client client = appState.getClients().stream()
                    .filter(c -> c.getId().equals(e.getIdClient()))
                    .findFirst()
                    .orElse(null);

            if (livre != null && client != null) {
                String statut = e.estEnRetard() ?
                        "🔴 EN RETARD (" + e.getJoursRetard() + " jour(s))" :
                        "🟢 Dans les délais";

                empruntsObs.add(new EmpruntGlobal(
                        livre.getTitre(),
                        livre.getAuteur(),
                        client.getNomComplet(),
                        client.getTelephone(),
                        e.getDate(),
                        e.getDateRetour(),
                        statut
                ));
            }
        }

        tableGlobal.setItems(empruntsObs);

        // 🔥 MESSAGE INFORMATIF AVEC STATISTIQUES POUR LE CLIENT
        if (appState.isClientConnecte()) {
            int nbEmprunts = empruntsAAfficher.size();
            int nbRetard = (int) empruntsAAfficher.stream()
                    .filter(Emprunt::estEnRetard)
                    .count();

            if (nbEmprunts == 0) {
                showAlert("📋 Vous n'avez aucun emprunt en cours.");
            } else {
                String message = String.format(
                        "📋 Vos emprunts en cours :\n" +
                                "• Total : %d emprunt(s)\n" +
                                "• En retard : %d emprunt(s)\n" +
                                "• Dans les délais : %d emprunt(s)",
                        nbEmprunts, nbRetard, nbEmprunts - nbRetard
                );
                showAlert(message);
            }
        }
    }

    // 🔥 AFFICHER LES STATISTIQUES SEULEMENT POUR L'ADMIN
    private void mettreAJourStatistiques() {
        try {
            ApplicationState appState = ApplicationState.getInstance();

            // 🔥 AFFICHER UNIQUEMENT POUR L'ADMIN
            if (!appState.isAdminConnecte()) {
                if (lblTotalLivres != null) {
                    lblTotalLivres.setText("📚 Bibliothèque");
                    lblLivresDisponibles.setText("");
                    lblEmpruntsActifs.setText("");
                    lblClients.setText("");
                }
                return;
            }

            // 🔥 CORRECTION : Utiliser les statistiques globales cohérentes
            Map<String, Integer> stats = appState.getStatistiquesGlobales();

            int totalLivres = stats.get("total");
            int livresDisponibles = stats.get("disponibles");
            int empruntsActifs = stats.get("empruntes");
            int clientsEnregistres = appState.getClients().size();

            // Mise à jour des labels
            if (lblTotalLivres != null) {
                lblTotalLivres.setText("📚 Total: " + totalLivres);
            }
            if (lblLivresDisponibles != null) {
                lblLivresDisponibles.setText("🟢 Disponibles: " + livresDisponibles);
            }
            if (lblEmpruntsActifs != null) {
                lblEmpruntsActifs.setText("📖 Emprunts: " + empruntsActifs);
            }
            if (lblClients != null) {
                lblClients.setText("👥 Clients: " + clientsEnregistres);
            }

            System.out.println("📊 Statistiques ADMIN mises à jour - Total: " + totalLivres +
                    ", Disponibles: " + livresDisponibles +
                    ", Emprunts: " + empruntsActifs +
                    ", Clients: " + clientsEnregistres);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la mise à jour des statistiques: " + e.getMessage());
        }
    }

    // 🔥 METTRE À JOUR LES STATISTIQUES PERSONNELLES
    private void mettreAJourStatistiquesPersonnelles() {
        if (appState.isClientConnecte() && appState.getClientActuel() != null) {
            Client client = appState.getClientActuel();
            List<Emprunt> empruntsClient = appState.getEmpruntsClientActuel();

            int empruntsTotal = empruntsClient.size();
            int empruntsEnRetard = (int) empruntsClient.stream()
                    .filter(Emprunt::estEnRetard)
                    .count();
            int empruntsEnCours = empruntsTotal; // Tous les emprunts sont en cours

            // Mettre à jour l'affichage principal
            if (txtClientConnecte != null) {
                String infoClient = String.format("👤 %s", client.getNomComplet());
                txtClientConnecte.setText(infoClient);
            }

            // 🔥 METTRE À JOUR LES LABELS
            if (lblEmpruntsTotal != null) {
                lblEmpruntsTotal.setText(String.valueOf(empruntsTotal));
            }
            if (lblEmpruntsEnCours != null) {
                lblEmpruntsEnCours.setText(String.valueOf(empruntsEnCours));
            }
            if (lblEmpruntsRetard != null) {
                lblEmpruntsRetard.setText(String.valueOf(empruntsEnRetard));
            }
        } else {
            // Réinitialiser si aucun client connecté
            if (lblEmpruntsTotal != null) lblEmpruntsTotal.setText("0");
            if (lblEmpruntsEnCours != null) lblEmpruntsEnCours.setText("0");
            if (lblEmpruntsRetard != null) lblEmpruntsRetard.setText("0");
        }
    }

    // 🔥 AJOUTER UN EMPRUNT
    @FXML
    private void onAddEmprunt() {
        try {
            Client client = null;
            if (appState.isClientConnecte()) {
                client = appState.getClientActuel();
            } else {
                showAlert("❌ Aucun client connecté ! Veuillez vous connecter.");
                return;
            }

            Livre livreSelectionne = comboLivres.getValue();

            if (client == null || livreSelectionne == null) {
                showAlert("Veuillez choisir un livre !");
                return;
            }

            Livre livrePrincipal = appState.getLivres().stream()
                    .filter(l -> l.getId().equals(livreSelectionne.getId()))
                    .findFirst().orElse(null);

            if (livrePrincipal == null) {
                showAlert("❌ Erreur : Livre non trouvé dans la base de données !");
                return;
            }

            if (livrePrincipal.getCopiesDisponibles() <= 0) {
                showAlert("❌ Ce livre n'est plus disponible !");
                return;
            }

            boolean dejaEmprunte = appState.getEmpruntsClient(client.getId()).stream()
                    .anyMatch(e -> e.getIdLivre().equals(livrePrincipal.getId()));

            if (dejaEmprunte) {
                showAlert("⚠️ Vous avez déjà emprunté ce livre !");
                return;
            }

            String date = datePicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);
            Emprunt nouvelEmprunt = new Emprunt(livrePrincipal.getId(), client.getId(), date);

            appState.ajouterEmprunt(nouvelEmprunt);

            int nouvellesDisponibles = livrePrincipal.getCopiesDisponibles() - 1;
            livrePrincipal.setCopiesDisponibles(nouvellesDisponibles);

            appState.sauvegarderEmprunts();
            appState.sauvegarderLivres();

            // 🔥 METTRE À JOUR LES STATISTIQUES PERSONNELLES
            mettreAJourStatistiquesPersonnelles();
            // 🔥 METTRE À JOUR LES STATISTIQUES GLOBALES
            mettreAJourStatistiques();

            showAlert("✅ Emprunt enregistré avec succès !\nDate de retour: " + nouvelEmprunt.getDateRetour());

            onRefresh();

        } catch (Exception e) {
            showAlert("❌ Erreur lors de l'emprunt : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 🔥 SUPPRIMER UN EMPRUNT
    @FXML
    private void onDeleteEmprunt() {
        EmpruntGlobal empruntGlobal = tableGlobal.getSelectionModel().getSelectedItem();
        if (empruntGlobal == null) {
            showAlert("Veuillez sélectionner un emprunt à supprimer !");
            return;
        }

        try {
            Emprunt empruntASupprimer = null;
            Livre livreConcerne = null;

            List<Emprunt> empruntsRecherche;
            if (appState.isAdminConnecte()) {
                empruntsRecherche = appState.getTousEmprunts();
            } else {
                empruntsRecherche = appState.getEmpruntsClientActuel();
            }

            for (Emprunt emp : empruntsRecherche) {
                Livre livre = appState.getLivres().stream()
                        .filter(l -> l.getId().equals(emp.getIdLivre()))
                        .findFirst().orElse(null);

                if (livre != null &&
                        livre.getTitre().equals(empruntGlobal.getTitreLivre()) &&
                        emp.getDate().equals(empruntGlobal.getDate())) {
                    empruntASupprimer = emp;
                    livreConcerne = livre;
                    break;
                }
            }

            if (empruntASupprimer != null) {
                appState.supprimerEmprunt(empruntASupprimer);
                appState.sauvegarderEmprunts();
            }

            if (livreConcerne != null) {
                int nouvellesDisponibles = livreConcerne.getCopiesDisponibles() + 1;
                livreConcerne.setCopiesDisponibles(nouvellesDisponibles);
                appState.sauvegarderLivres();
            }

            // 🔥 METTRE À JOUR LES STATISTIQUES PERSONNELLES
            mettreAJourStatistiquesPersonnelles();
            // 🔥 METTRE À JOUR LES STATISTIQUES GLOBALES
            mettreAJourStatistiques();

            showAlert("✅ Emprunt retourné !");
            onRefresh();

        } catch (Exception ex) {
            showAlert("❌ Erreur : " + ex.getMessage());
        }
    }

    @FXML
    private void onRefresh() {
        try {
            appState.rafraichirDonnees();

            livresObs.setAll(appState.getLivres());
            livresFiltres.setAll(livresObs);
            tableLivres.setItems(livresFiltres);

            peuplerFiltres();
            appliquerFiltres();
            chargerEmpruntsEnCours();
            mettreAJourStatistiques();

            // 🔥 METTRE À JOUR LES STATISTIQUES PERSONNELLES
            mettreAJourStatistiquesPersonnelles();

            tableLivres.refresh();

            showAlert("🔄 Données actualisées avec succès !");
        } catch (Exception e) {
            showAlert("❌ Erreur lors de l'actualisation : " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // CLASSE INTERNE POUR LE TABLEAU DES EMPRUNTS
    public static class EmpruntGlobal {
        private final String titreLivre;
        private final String auteur;
        private final String nomClient;
        private final String telephoneClient;
        private final String date;
        private final String dateRetour;
        private final String statut;

        public EmpruntGlobal(String titreLivre, String auteur, String nomClient, String telephoneClient,
                             String date, String dateRetour, String statut) {
            this.titreLivre = titreLivre;
            this.auteur = auteur;
            this.nomClient = nomClient;
            this.telephoneClient = telephoneClient;
            this.date = date;
            this.dateRetour = dateRetour;
            this.statut = statut;
        }

        public String getTitreLivre() { return titreLivre; }
        public String getAuteur() { return auteur; }
        public String getNomClient() { return nomClient; }
        public String getTelephoneClient() { return telephoneClient; }
        public String getDate() { return date; }
        public String getDateRetour() { return dateRetour; }
        public String getStatut() { return statut; }
    }
}
