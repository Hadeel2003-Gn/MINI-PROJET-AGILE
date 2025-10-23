package com.example.hadeellina.models;


public class Client {
    private String id;
    private String nom;
    private String prenom;
    private String telephone;
    private String adresse;
    private String email;
    private String password; // 🔐 Nouveau champ mot de passe

    public Client(String id, String nom, String prenom, String telephone, String adresse, String email, String password) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.adresse = adresse;
        this.email = email;
        this.password = password;
    }

    // Getters
    public String getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getTelephone() { return telephone; }
    public String getAdresse() { return adresse; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    // 🔥 MÉTHODE POUR AVOIR LE NOM COMPLET FORMATÉ
    public String getNomComplet() {
        if (nom == null) nom = "";
        if (prenom == null) prenom = "";

        String nomFormate = nom.toUpperCase();
        String prenomFormate = prenom.length() > 0 ?
                prenom.substring(0, 1).toUpperCase() + (prenom.length() > 1 ? prenom.substring(1).toLowerCase() : "")
                : "";

        return nomFormate + " " + prenomFormate;
    }

    @Override
    public String toString() {
        return getNomComplet(); // 🔥 Utiliser nom complet formaté
    }
}