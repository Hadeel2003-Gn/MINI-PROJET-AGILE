package com.example.hadeellina.models;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;
import java.util.HashMap;
import java.util.Map;

public class Livre {

    private final StringProperty id;
    private final StringProperty titre;
    private final StringProperty auteur;
    private final StringProperty annee;
    private final IntegerProperty nombreCopies;
    private final IntegerProperty copiesDisponibles;
    private final StringProperty langue;

    // Map pour les informations personnalisées dynamiques
    private Map<String, String> informationsPersonnalisees;

    public Livre(String id, String titre, String auteur, String annee, int nombreCopies) {
        this.id = new SimpleStringProperty(id);
        this.titre = new SimpleStringProperty(titre);
        this.auteur = new SimpleStringProperty(auteur);
        this.annee = new SimpleStringProperty(annee);
        this.nombreCopies = new SimpleIntegerProperty(nombreCopies);
        this.copiesDisponibles = new SimpleIntegerProperty(nombreCopies);
        this.langue = new SimpleStringProperty("Français");
        this.informationsPersonnalisees = new HashMap<>();
    }

    // Properties pour TableView
    public StringProperty idProperty() { return id; }
    public StringProperty titreProperty() { return titre; }
    public StringProperty auteurProperty() { return auteur; }
    public StringProperty anneeProperty() { return annee; }
    public IntegerProperty nombreCopiesProperty() { return nombreCopies; }
    public IntegerProperty copiesDisponiblesProperty() { return copiesDisponibles; }
    public StringProperty langueProperty() { return langue; }

    // Getters et setters
    public String getId() { return id.get(); }
    public String getTitre() { return titre.get(); }
    public String getAuteur() { return auteur.get(); }
    public String getAnnee() { return annee.get(); }
    public int getNombreCopies() { return nombreCopies.get(); }
    public int getCopiesDisponibles() { return copiesDisponibles.get(); }
    public String getLangue() { return langue.get(); }
    public void setLangue(String langue) { this.langue.set(langue); }

    public void setNombreCopies(int nb) {
        nombreCopies.set(nb);
        // 🔥 CORRECTION : S'assurer que les copies disponibles ne dépassent pas le total
        if (getCopiesDisponibles() > nb) {
            setCopiesDisponibles(nb);
        }
    }

    public void setCopiesDisponibles(int nb) {
        // 🔥 CORRECTION : Validation pour éviter les valeurs négatives ou supérieures au total
        if (nb < 0) {
            copiesDisponibles.set(0);
        } else if (nb > getNombreCopies()) {
            copiesDisponibles.set(getNombreCopies());
        } else {
            copiesDisponibles.set(nb);
        }
    }

    // Méthodes pour les informations personnalisées
    public Map<String, String> getInformationsPersonnalisees() {
        return new HashMap<>(informationsPersonnalisees);
    }

    public void setInformationsPersonnalisees(Map<String, String> infos) {
        this.informationsPersonnalisees = new HashMap<>(infos);
    }

    public void ajouterInformationPersonnalisee(String cle, String valeur) {
        this.informationsPersonnalisees.put(cle, valeur);
    }

    public String getInformationPersonnalisee(String cle) {
        return this.informationsPersonnalisees.get(cle);
    }

    public void supprimerInformationPersonnalisee(String cle) {
        this.informationsPersonnalisees.remove(cle);
    }

    // 🔥 CORRECTION : Méthode améliorée pour calculer les copies empruntées
    public int getCopiesEmpruntees() {
        int empruntees = nombreCopies.get() - copiesDisponibles.get();
        // S'assurer que le résultat n'est pas négatif
        return Math.max(0, empruntees);
    }

    public boolean isDisponible() {
        return copiesDisponibles.get() > 0;
    }

    // 🔥 NOUVELLE MÉTHODE : Validation de cohérence des données
    public boolean estCoherent() {
        return getCopiesDisponibles() >= 0 &&
                getCopiesDisponibles() <= getNombreCopies() &&
                getCopiesEmpruntees() >= 0 &&
                getCopiesEmpruntees() <= getNombreCopies();
    }

    @Override
    public String toString() {
        return titre.get();
    }
}