package com.example.hadeellina.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Emprunt {
    private String idLivre;
    private String idClient;
    private String date; // yyyy-MM-dd
    private String dateRetour; // yyyy-MM-dd

    public Emprunt(String idLivre, String idClient, String date) {
        this.idLivre = idLivre;
        this.idClient = idClient;
        this.date = date;
        // Calcul automatique de la date de retour (3 jours après la date d'emprunt)
        this.dateRetour = calculerDateRetour(date);
    }

    public Emprunt(String idLivre, String idClient, String date, String dateRetour) {
        this.idLivre = idLivre;
        this.idClient = idClient;
        this.date = date;
        this.dateRetour = dateRetour;
    }

    public String getIdLivre() { return idLivre; }
    public String getIdClient() { return idClient; }
    public String getDate() { return date; }
    public String getDateRetour() { return dateRetour; }

    // Méthode pour calculer automatiquement la date de retour (3 jours)
    private String calculerDateRetour(String dateEmprunt) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dateEmprunt, formatter);
            LocalDate dateRetour = date.plusDays(3); // 3 jours de délai
            return dateRetour.format(formatter);
        } catch (Exception e) {
            // En cas d'erreur, retourner une date par défaut
            return LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }

    // Méthode pour vérifier si l'emprunt est en retard
    public boolean estEnRetard() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dateRetourPrevue = LocalDate.parse(this.dateRetour, formatter);
            LocalDate aujourdhui = LocalDate.now();
            return aujourdhui.isAfter(dateRetourPrevue);
        } catch (Exception e) {
            return false;
        }
    }

    // Méthode pour obtenir le nombre de jours de retard
    public int getJoursRetard() {
        if (!estEnRetard()) {
            return 0;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dateRetourPrevue = LocalDate.parse(this.dateRetour, formatter);
            LocalDate aujourdhui = LocalDate.now();
            return (int) java.time.temporal.ChronoUnit.DAYS.between(dateRetourPrevue, aujourdhui);
        } catch (Exception e) {
            return 0;
        }
    }
}