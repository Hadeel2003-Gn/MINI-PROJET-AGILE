package com.example.hadeellina.utils;

import com.example.tp1gd.models.Livre;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CSVImporter {

    public static List<Livre> importerLivresDepuisCSV(String filePath) {
        List<Livre> livres = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            String ligne;
            boolean premiereLigne = true;
            int compteur = 0;
            int livresRejetes = 0;

            System.out.println("=== DÉBUT IMPORTATION CSV ===");

            while ((ligne = br.readLine()) != null) {
                if (premiereLigne) {
                    premiereLigne = false;
                    System.out.println("En-tête détecté: " + ligne);
                    analyserEntete(ligne);
                    continue;
                }

                ligne = nettoyerLigne(ligne);
                if (ligne.trim().isEmpty()) continue;

                Livre livre = creerLivreDepuisLigne(ligne, compteur);
                if (livre != null) {
                    livres.add(livre);
                    compteur++;

                    if (compteur % 100 == 0) {
                        System.out.println("📚 " + compteur + " livres importés...");
                    }
                } else {
                    livresRejetes++;
                }
            }

            System.out.println("✅ IMPORTATION TERMINÉE : " + compteur + " livres importés, " + livresRejetes + " rejetés");

        } catch (Exception e) {
            System.err.println("❌ Erreur importation UTF-8: " + e.getMessage());
            e.printStackTrace();
            return importerAvecEncodageAlternatif(filePath);
        }

        return livres;
    }

    private static void analyserEntete(String entete) {
        System.out.println("\n📋 ANALYSE DE L'EN-TÊTE CSV:");
        String[] colonnes = entete.split(",");
        for (int i = 0; i < colonnes.length && i < 10; i++) {
            System.out.println("  Colonne " + i + ": '" + colonnes[i].trim() + "'");
        }
        System.out.println();
    }

    private static List<Livre> importerAvecEncodageAlternatif(String filePath) {
        List<Livre> livres = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String ligne;
            boolean premiereLigne = true;
            int compteur = 0;
            int livresRejetes = 0;

            System.out.println("=== ESSAI AVEC ENCODAGE SYSTÈME ===");

            while ((ligne = br.readLine()) != null) {
                if (premiereLigne) {
                    premiereLigne = false;
                    continue;
                }

                ligne = nettoyerLigne(ligne);
                if (ligne.trim().isEmpty()) continue;

                Livre livre = creerLivreDepuisLigne(ligne, compteur);
                if (livre != null) {
                    livres.add(livre);
                    compteur++;

                    if (compteur % 100 == 0) {
                        System.out.println("📚 " + compteur + " livres importés (encodage alternatif)...");
                    }
                } else {
                    livresRejetes++;
                }
            }

            System.out.println("📚 " + compteur + " livres importés avec encodage alternatif, " + livresRejetes + " rejetés");

        } catch (Exception e) {
            System.err.println("❌ Échec avec encodage alternatif: " + e.getMessage());
        }

        return livres;
    }

    private static String nettoyerLigne(String ligne) {
        if (ligne == null) return "";
        return ligne.replaceAll("[^\\x20-\\x7E\\x80-\\xFF]", "").trim();
    }

    private static Livre creerLivreDepuisLigne(String ligne, int index) {
        try {
            // Gérer les guillemets et virgules dans le CSV
            String[] colonnes = parseCSVLine(ligne);

            if (colonnes.length < 1) {
                System.out.println("⚠️ Ligne " + index + " ignorée (trop courte)");
                return null;
            }

            // Afficher les 5 premières lignes pour debug
            if (index < 5) {
                System.out.println("\n🔍 DEBUG Ligne " + index + ":");
                for (int i = 0; i < Math.min(colonnes.length, 5); i++) {
                    System.out.println("  Col[" + i + "]: '" + colonnes[i] + "'");
                }
            }

            // 🔥 EXTRACTION INTELLIGENTE
            String titre = extraireTitre(colonnes);
            String auteur = extraireAuteur(colonnes, titre);
            String annee = extraireAnnee(colonnes);

            // Validation du titre
            if (titre.isEmpty() || titre.length() < 2 ||
                    titre.equalsIgnoreCase("unknown") || titre.matches("^\\d+$")) {
                System.out.println("❌ Ligne " + index + " rejetée: titre invalide '" + titre + "'");
                return null;
            }

            // Créer le livre
            String id = "L_" + System.currentTimeMillis() + "_" + index;
            int copies = 1 + (index % 3);

            Livre livre = new Livre(id, titre, auteur, annee, copies);
            livre.setLangue("Anglais");

            // Ajouter des informations
            livre.ajouterInformationPersonnalisee("ISBN", genererISBN(index));
            livre.ajouterInformationPersonnalisee("Pages", String.valueOf(200 + (index * 10) % 500));
            livre.ajouterInformationPersonnalisee("Genre", genererGenreIntelligent(titre));
            livre.ajouterInformationPersonnalisee("Editeur", genererEditeur(index));

            // Afficher les 10 premiers livres
            if (index < 10) {
                System.out.println("✅ Livre " + index + ": '" + titre + "' par '" + auteur + "'");
            }

            return livre;

        } catch (Exception e) {
            System.err.println("❌ Erreur ligne " + index + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // 🔥 PARSER CSV AVEC GESTION DES GUILLEMETS
    private static String[] parseCSVLine(String ligne) {
        List<String> resultat = new ArrayList<>();
        StringBuilder champ = new StringBuilder();
        boolean entreGuillemets = false;

        for (int i = 0; i < ligne.length(); i++) {
            char c = ligne.charAt(i);

            if (c == '"') {
                entreGuillemets = !entreGuillemets;
            } else if (c == ',' && !entreGuillemets) {
                resultat.add(champ.toString().trim());
                champ = new StringBuilder();
            } else {
                champ.append(c);
            }
        }
        resultat.add(champ.toString().trim());

        return resultat.toArray(new String[0]);
    }

    // 🔥 EXTRAIRE LE TITRE (colonne 0 ou 1 selon structure)
    private static String extraireTitre(String[] colonnes) {
        if (colonnes.length == 0) return "";

        String col0 = colonnes[0].trim();

        // Si col0 est un nombre, c'est probablement un ID, prendre col1
        if (col0.matches("^\\d+$") && colonnes.length > 1) {
            return nettoyerTitre(colonnes[1].trim());
        }

        // Sinon prendre col0
        return nettoyerTitre(col0);
    }

    // 🔥 EXTRAIRE L'AUTEUR (détecter automatiquement)
    private static String extraireAuteur(String[] colonnes, String titre) {
        // Essayer différentes colonnes
        for (int i = 1; i < Math.min(colonnes.length, 5); i++) {
            String col = colonnes[i].trim();

            // Ignorer si c'est le titre ou un nombre
            if (col.isEmpty() || col.equals(titre) || col.matches("^\\d+$")) {
                continue;
            }

            // Si la colonne contient un nom (pas trop long)
            if (col.length() > 3 && col.length() < 100 && !col.contains("(")) {
                // Vérifier si ça ressemble à un nom d'auteur
                if (ressembleAUnAuteur(col)) {
                    return col;
                }
            }
        }

        // Détection intelligente depuis le titre
        return detecterAuteurDepuisTitre(titre);
    }

    private static boolean ressembleAUnAuteur(String texte) {
        // Un auteur contient généralement des espaces et des lettres
        return texte.matches(".*[A-Z][a-z]+.*") &&
                (texte.contains(" ") || texte.contains("."));
    }

    private static String detecterAuteurDepuisTitre(String titre) {
        String titreLower = titre.toLowerCase();

        if (titreLower.contains("harry potter")) return "J.K. Rowling";
        if (titreLower.contains("lord of the rings") || titreLower.contains("hobbit")) return "J.R.R. Tolkien";
        if (titreLower.contains("hunger games")) return "Suzanne Collins";
        if (titreLower.contains("twilight")) return "Stephenie Meyer";
        if (titreLower.contains("divergent")) return "Veronica Roth";
        if (titreLower.contains("game of thrones") || titreLower.contains("song of ice")) return "George R.R. Martin";
        if (titreLower.contains("fifty shades")) return "E.L. James";
        if (titreLower.contains("gone girl")) return "Gillian Flynn";
        if (titreLower.contains("fault in our stars")) return "John Green";
        if (titreLower.contains("maze runner")) return "James Dashner";
        if (titreLower.contains("percy jackson")) return "Rick Riordan";
        if (titreLower.contains("da vinci code")) return "Dan Brown";
        if (titreLower.contains("sherlock")) return "Arthur Conan Doyle";
        if (titreLower.contains("pride and prejudice")) return "Jane Austen";
        if (titreLower.contains("1984") || titreLower.contains("animal farm")) return "George Orwell";

        return "Auteur Inconnu";
    }

    private static String nettoyerTitre(String titre) {
        if (titre == null || titre.isEmpty()) return "";

        // Supprimer les guillemets
        titre = titre.replaceAll("^\"|\"$", "");

        // Supprimer les informations de série entre parenthèses
        if (titre.contains("(")) {
            int indexParenthese = titre.indexOf("(");
            String titrePrincipal = titre.substring(0, indexParenthese).trim();
            if (titrePrincipal.length() > 3) {
                return titrePrincipal;
            }
        }

        return titre.trim();
    }

    private static String extraireAnnee(String[] colonnes) {
        // Chercher une année dans n'importe quelle colonne
        for (String col : colonnes) {
            String annee = extraireAnneeDepuisTexte(col);
            if (!annee.equals("Inconnue")) {
                return annee;
            }
        }
        return "Inconnue";
    }

    private static String extraireAnneeDepuisTexte(String texte) {
        if (texte == null || texte.isEmpty()) return "Inconnue";

        try {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\b(19|20)\\d{2}\\b");
            java.util.regex.Matcher matcher = pattern.matcher(texte);

            if (matcher.find()) {
                String annee = matcher.group();
                int anneeInt = Integer.parseInt(annee);
                if (anneeInt >= 1500 && anneeInt <= 2025) {
                    return annee;
                }
            }
        } catch (Exception e) {
            // Ignorer
        }

        return "Inconnue";
    }

    private static String genererISBN(int index) {
        return "978-" + (1000 + index) + "-" + (10000 + index) + "-" + (index % 10);
    }

    private static String genererGenreIntelligent(String titre) {
        String titreLower = titre.toLowerCase();

        if (titreLower.contains("harry potter") || titreLower.contains("lord of the rings"))
            return "Fantasy";
        if (titreLower.contains("hunger games") || titreLower.contains("divergent"))
            return "Dystopian";
        if (titreLower.contains("twilight") || titreLower.contains("fifty shades"))
            return "Romance";
        if (titreLower.contains("game of thrones"))
            return "Epic Fantasy";
        if (titreLower.contains("gone girl") || titreLower.contains("da vinci"))
            return "Thriller";
        if (titreLower.contains("fault in our stars"))
            return "Contemporary";
        if (titreLower.contains("1984"))
            return "Dystopian Fiction";

        return "Fiction";
    }

    private static String genererEditeur(int index) {
        String[] editeurs = {"Scholastic", "Bloomsbury", "Penguin Random House", "HarperCollins",
                "Simon & Schuster", "Macmillan", "Hachette", "Vintage",
                "Del Rey", "Tor Books"};
        return editeurs[index % editeurs.length];
    }

    public static String verifierFichierCSV(String filePath) {
        try {
            File file = new File(filePath);

            if (!file.exists()) {
                return "❌ Fichier non trouvé: " + filePath;
            }

            int nombreLignes = 0;
            int nombreLivresEstime = 0;
            String premiereDataLigne = null;

            try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                String ligne;
                boolean premiereLigne = true;

                while ((ligne = br.readLine()) != null) {
                    nombreLignes++;
                    if (premiereLigne) {
                        premiereLigne = false;
                        continue;
                    }
                    if (!ligne.trim().isEmpty()) {
                        nombreLivresEstime++;
                        if (premiereDataLigne == null) {
                            premiereDataLigne = ligne.length() > 200 ? ligne.substring(0, 200) + "..." : ligne;
                        }
                    }
                }
            } catch (Exception e) {
                // Réessayer sans UTF-8
            }

            StringBuilder resultat = new StringBuilder();
            resultat.append("✅ Fichier accessible\n");
            resultat.append("📁 Chemin: ").append(file.getAbsolutePath()).append("\n");
            resultat.append("📊 Taille: ").append(String.format("%,d", file.length())).append(" bytes\n");
            resultat.append("📝 Lignes: ").append(String.format("%,d", nombreLignes)).append("\n");
            resultat.append("📚 Livres estimés: ").append(String.format("%,d", nombreLivresEstime)).append("\n");
            if (premiereDataLigne != null) {
                resultat.append("👀 Premier livre: ").append(premiereDataLigne).append("\n");
            }

            return resultat.toString();

        } catch (Exception e) {
            return "❌ Erreur d'accès: " + e.getMessage();
        }
    }

    public static List<Livre> genererLivresTest() {
        List<Livre> livres = new ArrayList<>();

        String[][] livresTest = {
                {"Harry Potter à l'école des sorciers", "J.K. Rowling", "1997"},
                {"Le Seigneur des Anneaux", "J.R.R. Tolkien", "1954"},
                {"1984", "George Orwell", "1949"},
                {"Le Petit Prince", "Antoine de Saint-Exupéry", "1943"},
                {"Dune", "Frank Herbert", "1965"},
                {"Fondation", "Isaac Asimov", "1951"},
                {"Les Misérables", "Victor Hugo", "1862"},
                {"Orgueil et Préjugés", "Jane Austen", "1813"},
                {"L'Étranger", "Albert Camus", "1942"},
                {"Germinal", "Émile Zola", "1885"}
        };

        for (int i = 0; i < livresTest.length; i++) {
            String[] data = livresTest[i];
            String id = "L_TEST_" + System.currentTimeMillis() + "_" + i;
            int copies = 1 + (i % 3);

            Livre livre = new Livre(id, data[0], data[1], data[2], copies);
            livre.setLangue(i % 2 == 0 ? "Français" : "Anglais");

            livre.ajouterInformationPersonnalisee("ISBN", genererISBN(i));
            livre.ajouterInformationPersonnalisee("Pages", String.valueOf(250 + (i * 30)));
            livre.ajouterInformationPersonnalisee("Genre", genererGenreIntelligent(data[0]));
            livre.ajouterInformationPersonnalisee("Editeur", genererEditeur(i));

            livres.add(livre);
        }

        System.out.println("🎲 " + livres.size() + " livres de test générés!");
        return livres;
    }
}