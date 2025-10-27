package com.example.hadeellina;

import com.example.hadeellina.utils.XPathXQueryUtils;
import java.util.List;
import java.util.Map;

/**
 * Classe de test pour démontrer l'utilisation de XPath et XQuery
 * sur les fichiers XML de la bibliothèque
 */
public class TestXPathXQuery {

    private static final String LIVRE_XML = "livre.xml";

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("        DÉMONSTRATION XPATH & XQUERY - BIBLIOTHÈQUE");
        System.out.println("=".repeat(70));
        System.out.println();

        // Test 1: Recherche par titre
        test1_RechercheParTitre();

        // Test 2: Recherche par auteur
        test2_RechercheParAuteur();

        // Test 3: Recherche par année
        test3_RechercheParAnnee();

        // Test 4: Livres disponibles
        test4_LivresDisponibles();

        // Test 5: Recherche entre années
        test5_RechercheEntreAnnees();

        // Test 6: Recherche multi-critères
        test6_RechercheMultiCriteres();

        // Test 7: Top livres empruntés
        test7_TopLivresEmpruntes();

        // Test 8: Statistiques par genre
        test8_StatistiquesParGenre();

        // Test 9: Requête XPath personnalisée
        test9_RequetePersonnalisee();

        System.out.println("\n" + "=".repeat(70));
        System.out.println("                    FIN DES TESTS");
        System.out.println("=".repeat(70));
    }

    private static void test1_RechercheParTitre() {
        System.out.println("\n📚 TEST 1: RECHERCHE PAR TITRE (XPath)");
        System.out.println("-".repeat(70));

        List<Map<String, String>> resultats = XPathXQueryUtils.rechercherLivresParTitre(LIVRE_XML, "Harry");

        System.out.println("Requête: Livres contenant 'Harry' dans le titre");
        System.out.println("Résultats trouvés: " + resultats.size());

        resultats.forEach(livre -> {
            System.out.println("  📖 " + livre.get("Titre") + " - " + livre.get("Auteur"));
        });
    }

    private static void test2_RechercheParAuteur() {
        System.out.println("\n✍️ TEST 2: RECHERCHE PAR AUTEUR (XPath)");
        System.out.println("-".repeat(70));

        List<Map<String, String>> resultats = XPathXQueryUtils.rechercherLivresParAuteur(LIVRE_XML, "Rowling");

        System.out.println("Requête: Livres de l'auteur contenant 'Rowling'");
        System.out.println("Résultats trouvés: " + resultats.size());

        resultats.forEach(livre -> {
            System.out.println("  📖 " + livre.get("Titre") + " (" + livre.get("Annee") + ")");
        });
    }

    private static void test3_RechercheParAnnee() {
        System.out.println("\n📅 TEST 3: RECHERCHE PAR ANNÉE (XPath)");
        System.out.println("-".repeat(70));

        List<Map<String, String>> resultats = XPathXQueryUtils.rechercherLivresParAnnee(LIVRE_XML, "1997");

        System.out.println("Requête: Livres publiés en 1997");
        System.out.println("Résultats trouvés: " + resultats.size());

        resultats.forEach(livre -> {
            System.out.println("  📖 " + livre.get("Titre") + " - " + livre.get("Auteur"));
        });
    }

    private static void test4_LivresDisponibles() {
        System.out.println("\n🟢 TEST 4: LIVRES DISPONIBLES (XPath)");
        System.out.println("-".repeat(70));

        List<Map<String, String>> resultats = XPathXQueryUtils.rechercherLivresDisponibles(LIVRE_XML);

        System.out.println("Requête: Livres avec au moins 1 copie disponible");
        System.out.println("Résultats trouvés: " + resultats.size());

        resultats.stream().limit(5).forEach(livre -> {
            System.out.println("  📖 " + livre.get("Titre") +
                    " - Disponibles: " + livre.get("CopiesDisponibles") +
                    "/" + livre.get("NombreCopies"));
        });

        if (resultats.size() > 5) {
            System.out.println("  ... et " + (resultats.size() - 5) + " autres livres");
        }
    }

    private static void test5_RechercheEntreAnnees() {
        System.out.println("\n📅 TEST 5: RECHERCHE ENTRE ANNÉES (XQuery)");
        System.out.println("-".repeat(70));

        List<Map<String, String>> resultats = XPathXQueryUtils.xqueryLivresEntreAnnees(LIVRE_XML, 1990, 2000);

        System.out.println("Requête: Livres publiés entre 1990 et 2000");
        System.out.println("Résultats trouvés: " + resultats.size());

        resultats.forEach(livre -> {
            System.out.println("  📖 " + livre.get("Titre") +
                    " (" + livre.get("Annee") + ") - " + livre.get("Auteur"));
        });
    }

    private static void test6_RechercheMultiCriteres() {
        System.out.println("\n🎯 TEST 6: RECHERCHE MULTI-CRITÈRES (XQuery Avancé)");
        System.out.println("-".repeat(70));

        List<Map<String, String>> resultats = XPathXQueryUtils.xqueryRechercheAvancee(
                LIVRE_XML,
                null,           // titre
                null,           // auteur
                "Fantasy",      // genre
                "Anglais",      // langue
                true            // disponibles uniquement
        );

        System.out.println("Requête: Livres Fantasy en Anglais, disponibles uniquement");
        System.out.println("Résultats trouvés: " + resultats.size());

        resultats.forEach(livre -> {
            System.out.println("  📖 " + livre.get("Titre") +
                    " - " + livre.get("Genre") +
                    " (" + livre.get("Langue") + ")");
        });
    }

    private static void test7_TopLivresEmpruntes() {
        System.out.println("\n🏆 TEST 7: TOP LIVRES EMPRUNTÉS (XQuery)");
        System.out.println("-".repeat(70));

        List<Map<String, String>> resultats = XPathXQueryUtils.xqueryTopLivresEmpruntes(LIVRE_XML, 5);

        System.out.println("Requête: Top 5 des livres les plus empruntés");
        System.out.println("Résultats trouvés: " + resultats.size());

        int rang = 1;
        for (Map<String, String> livre : resultats) {
            int total = Integer.parseInt(livre.getOrDefault("NombreCopies", "0"));
            int disponibles = Integer.parseInt(livre.getOrDefault("CopiesDisponibles", "0"));
            int empruntes = total - disponibles;

            System.out.println("  " + rang + ". 📖 " + livre.get("Titre") +
                    " - Empruntés: " + empruntes + "/" + total);
            rang++;
        }
    }

    private static void test8_StatistiquesParGenre() {
        System.out.println("\n📊 TEST 8: STATISTIQUES PAR GENRE (XQuery)");
        System.out.println("-".repeat(70));

        Map<String, Integer> stats = XPathXQueryUtils.xqueryStatistiquesParGenre(LIVRE_XML);

        System.out.println("Requête: Compter le nombre de livres par genre");
        System.out.println("Genres trouvés: " + stats.size());
        System.out.println();

        stats.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .forEach(entry -> {
                    System.out.println("  📚 " + String.format("%-20s", entry.getKey()) +
                            ": " + entry.getValue() + " livre(s)");
                });
    }

    private static void test9_RequetePersonnalisee() {
        System.out.println("\n💻 TEST 9: REQUÊTE XPATH PERSONNALISÉE");
        System.out.println("-".repeat(70));

        // Exemple: Livres avec plus de 2 copies totales
        String requete = "//Livre[number(NombreCopies) > 2]";
        List<Map<String, String>> resultats = XPathXQueryUtils.executerXPathQuery(LIVRE_XML, requete);

        System.out.println("Requête XPath: " + requete);
        System.out.println("Description: Livres avec plus de 2 copies");
        System.out.println("Résultats trouvés: " + resultats.size());
        System.out.println();

        resultats.stream().limit(5).forEach(livre -> {
            System.out.println("  📖 " + livre.get("Titre") +
                    " - Copies: " + livre.get("NombreCopies"));
        });

        if (resultats.size() > 5) {
            System.out.println("  ... et " + (resultats.size() - 5) + " autres livres");
        }
    }


    private static void testXQuery1_TousLesTitres() {
        System.out.println("\n📚 XQUERY 1: TOUS LES TITRES DE LIVRES");
        System.out.println("-".repeat(70));

        List<String> titres = XPathXQueryUtils.xqueryTousLesTitres(LIVRE_XML);

        System.out.println("Requête: Récupérer tous les titres de livres");
        System.out.println("Nombre de titres: " + titres.size());
        System.out.println();

        titres.forEach(titre -> System.out.println("  📖 " + titre));
    }

    private static void testXQuery2_DisponiblesSuperieurA2() {
        System.out.println("\n📚 XQUERY 2: LIVRES DISPONIBLES > 2");
        System.out.println("-".repeat(70));

        List<Map<String, String>> resultats = XPathXQueryUtils.xqueryLivresDisponiblesSuperieurA2(LIVRE_XML);

        System.out.println("Requête: Livres avec plus de 2 exemplaires disponibles");
        System.out.println("Résultats trouvés: " + resultats.size());
        System.out.println();

        resultats.forEach(livre -> {
            System.out.println("  📖 " + livre.get("Titre"));
            System.out.println("     Disponibles: " + livre.get("CopiesDisponibles") +
                    "/" + livre.get("NombreCopies"));
        });
    }

    private static void testXQuery3_NombreAvant2000() {
        System.out.println("\n📚 XQUERY 3: NOMBRE DE LIVRES AVANT 2000");
        System.out.println("-".repeat(70));

        int nombre = XPathXQueryUtils.xqueryNombreLivresAvant2000(LIVRE_XML);
        int total = XPathXQueryUtils.compterTotalLivres(LIVRE_XML);

        System.out.println("Requête: Compter les livres publiés avant l'an 2000");
        System.out.println("Résultat: " + nombre + " livres sur " + total);
        System.out.println("Pourcentage: " + String.format("%.1f", (nombre * 100.0 / total)) + "%");
    }
}

