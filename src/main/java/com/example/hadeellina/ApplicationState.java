package com.example.hadeellina;

import com.example.tp1gd.models.Client;
import com.example.tp1gd.models.Emprunt;
import com.example.tp1gd.models.Livre;

import java.util.*;

public class ApplicationState {
    private static ApplicationState instance;
    private List<Livre> livres = new ArrayList<>();
    private List<Client> clients = new ArrayList<>();

    // 🔥 CHANGEMENT : Map pour stocker les emprunts par client
    private Map<String, List<Emprunt>> empruntsParClient = new HashMap<>();

    private final String LIVRE_XML = "livre.xml";
    private final String CLIENT_XML = "client.xml";
    private final String EMPRUNT_XML = "emprunt.xml";

    // 🔐 PROPRIÉTÉS POUR L'AUTHENTIFICATION
    private boolean adminConnecte = false;
    private String nomAdmin = null;
    private boolean clientConnecte = false;
    private Client clientActuel = null;

    private ApplicationState() {
        chargerDonnees();
        // 🔥 CORRECTION : Valider la cohérence des données au chargement
        validerCohérenceDonnees();
    }

    public static ApplicationState getInstance() {
        if (instance == null) {
            instance = new ApplicationState();
        }
        return instance;
    }

    private void chargerDonnees() {
        try {
            livres = XMLUtils.lireLivres(LIVRE_XML);
            clients = XMLUtils.lireClients(CLIENT_XML);

            // 🔥 CHANGEMENT : Charger les emprunts et les organiser par client
            List<Emprunt> tousEmprunts = XMLUtils.lireEmprunts(EMPRUNT_XML);
            organiserEmpruntsParClient(tousEmprunts);

            System.out.println("✅ Données chargées : " +
                    livres.size() + " livres, " +
                    clients.size() + " clients, " +
                    empruntsParClient.size() + " clients avec emprunts");

        } catch (Exception e) {
            System.err.println("❌ Erreur chargement données: " + e.getMessage());
            livres = new ArrayList<>();
            clients = new ArrayList<>();
            empruntsParClient = new HashMap<>();
        }
    }

    // 🔥 NOUVELLE MÉTHODE : Organiser les emprunts par client
    private void organiserEmpruntsParClient(List<Emprunt> tousEmprunts) {
        empruntsParClient.clear();

        for (Emprunt emprunt : tousEmprunts) {
            String idClient = emprunt.getIdClient();

            if (!empruntsParClient.containsKey(idClient)) {
                empruntsParClient.put(idClient, new ArrayList<>());
            }

            empruntsParClient.get(idClient).add(emprunt);
        }
    }

    // 🔥 NOUVELLE MÉTHODE : Validation de la cohérence des données
    private void validerCohérenceDonnees() {
        System.out.println("🔍 Validation de la cohérence des données...");

        int totalIncoherences = 0;
        for (Livre livre : livres) {
            if (!livre.estCoherent()) {
                System.out.println("⚠️ Incohérence détectée pour le livre: " + livre.getTitre());
                totalIncoherences++;

                // 🔥 CORRECTION AUTOMATIQUE : Réajuster les copies disponibles
                if (livre.getCopiesDisponibles() > livre.getNombreCopies()) {
                    livre.setCopiesDisponibles(livre.getNombreCopies());
                    System.out.println("   → Copies disponibles ajustées: " + livre.getCopiesDisponibles());
                } else if (livre.getCopiesDisponibles() < 0) {
                    livre.setCopiesDisponibles(0);
                    System.out.println("   → Copies disponibles ajustées: 0");
                }
            }
        }

        if (totalIncoherences > 0) {
            System.out.println("🔧 " + totalIncoherences + " incohérences corrigées automatiquement");
            try {
                sauvegarderLivres();
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de la sauvegarde des corrections: " + e.getMessage());
            }
        } else {
            System.out.println("✅ Toutes les données sont cohérentes");
        }
    }

    // 🔥 NOUVELLE MÉTHODE : Calcul des statistiques globales cohérentes
    public Map<String, Integer> getStatistiquesGlobales() {
        Map<String, Integer> stats = new HashMap<>();

        int totalLivres = 0;
        int totalDisponibles = 0;
        int totalEmpruntes = 0;

        for (Livre livre : livres) {
            totalLivres += livre.getNombreCopies();
            totalDisponibles += livre.getCopiesDisponibles();
            totalEmpruntes += livre.getCopiesEmpruntees();
        }

        // 🔥 CORRECTION : S'assurer que la somme correspond
        if (totalDisponibles + totalEmpruntes != totalLivres) {
            System.out.println("⚠️ Incohérence dans les statistiques globales:");
            System.out.println("   Total livres: " + totalLivres);
            System.out.println("   Disponibles: " + totalDisponibles);
            System.out.println("   Empruntés: " + totalEmpruntes);
            System.out.println("   Différence: " + (totalLivres - (totalDisponibles + totalEmpruntes)));

            // Ajustement automatique
            totalEmpruntes = totalLivres - totalDisponibles;
            System.out.println("   → Empruntés ajustés: " + totalEmpruntes);
        }

        stats.put("total", totalLivres);
        stats.put("disponibles", totalDisponibles);
        stats.put("empruntes", totalEmpruntes);

        return stats;
    }

    // 🔥 NOUVELLE MÉTHODE : Mettre à jour les statistiques après un emprunt/retour
    public void mettreAJourStatistiquesApresOperation() {
        Map<String, Integer> stats = getStatistiquesGlobales();
        System.out.println("📊 Statistiques mises à jour - Total: " + stats.get("total") +
                ", Disponibles: " + stats.get("disponibles") +
                ", Empruntés: " + stats.get("empruntes"));
    }

    public void sauvegarderLivres() throws Exception {
        XMLUtils.saveLivres(livres, LIVRE_XML);
    }

    public void sauvegarderClients() throws Exception {
        XMLUtils.saveClients(clients, CLIENT_XML);
    }

    public void sauvegarderEmprunts() throws Exception {
        // 🔥 CHANGEMENT : Sauvegarder tous les emprunts de tous les clients
        List<Emprunt> tousEmprunts = new ArrayList<>();
        for (List<Emprunt> empruntsClient : empruntsParClient.values()) {
            tousEmprunts.addAll(empruntsClient);
        }
        XMLUtils.saveEmprunts(tousEmprunts, EMPRUNT_XML);
    }

    // 🔥 NOUVELLES MÉTHODES POUR GÉRER LES EMPRUNTS PAR CLIENT

    public List<Emprunt> getEmpruntsClient(String idClient) {
        return empruntsParClient.getOrDefault(idClient, new ArrayList<>());
    }

    public List<Emprunt> getEmpruntsClientActuel() {
        if (clientActuel != null) {
            return getEmpruntsClient(clientActuel.getId());
        }
        return new ArrayList<>();
    }

    public void ajouterEmprunt(Emprunt emprunt) {
        String idClient = emprunt.getIdClient();

        if (!empruntsParClient.containsKey(idClient)) {
            empruntsParClient.put(idClient, new ArrayList<>());
        }

        empruntsParClient.get(idClient).add(emprunt);
        // 🔥 CORRECTION : Mettre à jour les statistiques après l'opération
        mettreAJourStatistiquesApresOperation();
    }

    public void supprimerEmprunt(Emprunt emprunt) {
        String idClient = emprunt.getIdClient();

        if (empruntsParClient.containsKey(idClient)) {
            empruntsParClient.get(idClient).remove(emprunt);
            // 🔥 CORRECTION : Mettre à jour les statistiques après l'opération
            mettreAJourStatistiquesApresOperation();
        }
    }

    // 🔥 MÉTHODE POUR OBTENIR TOUS LES EMPRUNTS (pour l'admin)
    public List<Emprunt> getTousEmprunts() {
        List<Emprunt> tousEmprunts = new ArrayList<>();
        for (List<Emprunt> empruntsClient : empruntsParClient.values()) {
            tousEmprunts.addAll(empruntsClient);
        }
        return tousEmprunts;
    }

    // Les autres méthodes restent inchangées...
    public boolean isAdminConnecte() { return adminConnecte; }
    public void setAdminConnecte(boolean adminConnecte) { this.adminConnecte = adminConnecte; }
    public String getNomAdmin() { return nomAdmin != null ? nomAdmin : "Admin"; }
    public void setNomAdmin(String nomAdmin) { this.nomAdmin = nomAdmin; }
    public boolean isClientConnecte() { return clientConnecte; }
    public Client getClientActuel() { return clientActuel; }
    public void setClientActuel(Client clientActuel) {
        this.clientActuel = clientActuel;
        this.clientConnecte = (clientActuel != null);
    }

    public boolean connecterClient(String email, String password) {
        Optional<Client> clientOpt = clients.stream()
                .filter(client -> client.getEmail().equalsIgnoreCase(email) && client.getPassword().equals(password))
                .findFirst();

        if (clientOpt.isPresent()) {
            this.clientActuel = clientOpt.get();
            this.clientConnecte = true;
            return true;
        }
        return false;
    }

    public void deconnecterClient() {
        this.clientConnecte = false;
        this.clientActuel = null;
    }

    // Getters
    public List<Livre> getLivres() { return livres; }
    public List<Client> getClients() { return clients; }

    // 🔥 CHANGEMENT : Remplacer l'ancien getEmprunts()
    @Deprecated
    public List<Emprunt> getEmprunts() {
        return getTousEmprunts();
    }

    public void rafraichirDonnees() {
        chargerDonnees();
        validerCohérenceDonnees();
    }

    // Méthodes utilitaires
    public int getNombreLivresDisponibles() {
        return (int) livres.stream().filter(Livre::isDisponible).count();
    }

    public int getNombreEmpruntsActifs() {
        return getTousEmprunts().size();
    }
}