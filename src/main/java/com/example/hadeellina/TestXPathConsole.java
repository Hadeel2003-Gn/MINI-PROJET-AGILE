package com.example.hadeellina;



import com.example.hadeellina.utils.XPathXQueryUtils;
import java.util.List;
import java.util.Map;

public class TestXPathConsole {

    public static void main(String[] args) {
        String fichierXML = "livre.xml";

        System.out.println("=".repeat(60));
        System.out.println("🎯 TEST DES REQUÊTES XPATH DANS LA CONSOLE");
        System.out.println("=".repeat(60));

        // 1. Afficher tous les titres de livres
        System.out.println("\n1️⃣ TOUS LES TITRES DE LIVRES :");
        System.out.println("-".repeat(40));
        List<Map<String, String>> titres = XPathXQueryUtils.executerXPathQuery(fichierXML, "//Livre/Titre/text()");
        titres.forEach(titre -> System.out.println("📖 " + titre));

        // 2. Livres avec > 2 copies disponibles
        System.out.println("\n2️⃣ LIVRES AVEC PLUS DE 4 COPIES DISPONIBLES :");
        System.out.println("-".repeat(40));
        List<Map<String, String>> disponibles = XPathXQueryUtils.executerXPathQuery(fichierXML, "//Livre[number(CopiesDisponibles) > 4]");
        System.out.println("📊 Nombre trouvé : " + disponibles.size());
        disponibles.forEach(livre -> {
            System.out.println("📖 " + livre.get("Titre") +
                    " - Disponibles: " + livre.get("CopiesDisponibles") +
                    "/" + livre.get("NombreCopies"));
        });

        // 3. Nombre de livres avant 2000
        System.out.println("\n3️⃣ NOMBRE DE LIVRES AVANT L'AN 2000 :");
        System.out.println("-".repeat(40));
        int avant2000 = XPathXQueryUtils.compterTotalLivresAvant2000(fichierXML);
        System.out.println("📅 Résultat : " + avant2000 + " livre(s)");

        // Détails des livres avant 2000
        List<Map<String, String>> detailsAvant2000 = XPathXQueryUtils.executerXPathQuery(fichierXML, "//Livre[number(Annee) < 2000]");
        detailsAvant2000.forEach(livre -> {
            System.out.println("   📖 " + livre.get("Titre") + " (" + livre.get("Annee") + ")");
        });

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ TESTS TERMINÉS AVEC SUCCÈS");
        System.out.println("=".repeat(60));
    }
}
