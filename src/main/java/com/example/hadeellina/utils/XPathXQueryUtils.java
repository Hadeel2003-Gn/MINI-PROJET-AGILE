package com.example.hadeellina.utils;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;

public class XPathXQueryUtils {

    // =============== XPATH ===============

    /**
     * Recherche des livres par titre avec XPath
     */
    public static List<Map<String, String>> rechercherLivresParTitre(String filePath, String titre) {
        try {
            String xpathQuery = String.format(
                    "//Livre[contains(translate(Titre, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s')]",
                    titre.toLowerCase()
            );
            return executerXPathQuery(filePath, xpathQuery);
        } catch (Exception e) {
            System.err.println("❌ Erreur XPath recherche par titre: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Recherche des livres par auteur avec XPath
     */
    public static List<Map<String, String>> rechercherLivresParAuteur(String filePath, String auteur) {
        try {
            String xpathQuery = String.format(
                    "//Livre[contains(translate(Auteur, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s')]",
                    auteur.toLowerCase()
            );
            return executerXPathQuery(filePath, xpathQuery);
        } catch (Exception e) {
            System.err.println("❌ Erreur XPath recherche par auteur: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Recherche des livres par année avec XPath
     */
    public static List<Map<String, String>> rechercherLivresParAnnee(String filePath, String annee) {
        try {
            String xpathQuery = String.format("//Livre[Annee='%s']", annee);
            return executerXPathQuery(filePath, xpathQuery);
        } catch (Exception e) {
            System.err.println("❌ Erreur XPath recherche par année: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Recherche des livres par genre avec XPath
     */
    public static List<Map<String, String>> rechercherLivresParGenre(String filePath, String genre) {
        try {
            String xpathQuery = String.format("//Livre[Genre='%s']", genre);
            return executerXPathQuery(filePath, xpathQuery);
        } catch (Exception e) {
            System.err.println("❌ Erreur XPath recherche par genre: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Recherche des livres disponibles avec XPath
     */
    public static List<Map<String, String>> rechercherLivresDisponibles(String filePath) {
        try {
            String xpathQuery = "//Livre[number(CopiesDisponibles) > 0]";
            return executerXPathQuery(filePath, xpathQuery);
        } catch (Exception e) {
            System.err.println("❌ Erreur XPath recherche livres disponibles: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Compter le nombre total de livres avec XPath
     */
    public static int compterTotalLivres(String filePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            String expression = "count(//Livre)";
            Double count = (Double) xpath.compile(expression).evaluate(doc, XPathConstants.NUMBER);
            return count.intValue();
        } catch (Exception e) {
            System.err.println("❌ Erreur XPath comptage: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Exécuter une requête XPath personnalisée
     */
    public static List<Map<String, String>> executerXPathQuery(String filePath, String xpathQuery) {
        List<Map<String, String>> resultats = new ArrayList<>();

        try {
            File xmlFile = new File(filePath);
            if (!xmlFile.exists()) {
                System.err.println("⚠️ Fichier XML introuvable: " + filePath);
                return resultats;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            XPathExpression expr = xpath.compile(xpathQuery);
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            System.out.println("🔍 XPath Query: " + xpathQuery);
            System.out.println("📊 Résultats trouvés: " + nodes.getLength());

            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Map<String, String> livre = extraireDonneesLivre(element);
                    resultats.add(livre);
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur XPath: " + e.getMessage());
            e.printStackTrace();
        }

        return resultats;
    }

    // =============== XQUERY (Simulation avec XPath) ===============

    /**
     * XQuery: Trouver les livres entre deux années
     */
    public static List<Map<String, String>> xqueryLivresEntreAnnees(String filePath, int anneeDebut, int anneeFin) {
        try {
            String xpathQuery = String.format(
                    "//Livre[number(Annee) >= %d and number(Annee) <= %d]",
                    anneeDebut, anneeFin
            );
            return executerXPathQuery(filePath, xpathQuery);
        } catch (Exception e) {
            System.err.println("❌ Erreur XQuery: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * XQuery: Trouver les livres d'un genre et d'une langue spécifiques
     */
    public static List<Map<String, String>> xqueryLivresParGenreEtLangue(String filePath, String genre, String langue) {
        try {
            String xpathQuery = String.format(
                    "//Livre[Genre='%s' and Langue='%s']",
                    genre, langue
            );
            return executerXPathQuery(filePath, xpathQuery);
        } catch (Exception e) {
            System.err.println("❌ Erreur XQuery: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * XQuery: Recherche multi-critères avancée
     */
    public static List<Map<String, String>> xqueryRechercheAvancee(
            String filePath,
            String titre,
            String auteur,
            String genre,
            String langue,
            boolean disponibleUniquement) {

        try {
            StringBuilder query = new StringBuilder("//Livre[");
            List<String> conditions = new ArrayList<>();

            if (titre != null && !titre.isEmpty()) {
                conditions.add(String.format(
                        "contains(translate(Titre, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s')",
                        titre.toLowerCase()
                ));
            }

            if (auteur != null && !auteur.isEmpty()) {
                conditions.add(String.format(
                        "contains(translate(Auteur, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s')",
                        auteur.toLowerCase()
                ));
            }

            if (genre != null && !genre.isEmpty()) {
                conditions.add(String.format("Genre='%s'", genre));
            }

            if (langue != null && !langue.isEmpty()) {
                conditions.add(String.format("Langue='%s'", langue));
            }

            if (disponibleUniquement) {
                conditions.add("number(CopiesDisponibles) > 0");
            }

            if (conditions.isEmpty()) {
                query.append("true()");
            } else {
                query.append(String.join(" and ", conditions));
            }
            query.append("]");

            System.out.println("🔍 Recherche avancée XQuery: " + query.toString());
            return executerXPathQuery(filePath, query.toString());

        } catch (Exception e) {
            System.err.println("❌ Erreur XQuery recherche avancée: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * XQuery: Top N livres les plus empruntés
     */
    public static List<Map<String, String>> xqueryTopLivresEmpruntes(String filePath, int topN) {
        try {
            String xpathQuery = "//Livre[number(NombreCopies) - number(CopiesDisponibles) > 0]";
            List<Map<String, String>> resultats = executerXPathQuery(filePath, xpathQuery);

            // Trier par nombre de copies empruntées
            resultats.sort((a, b) -> {
                int empruntesA = calculerCopiesEmpruntees(a);
                int empruntesB = calculerCopiesEmpruntees(b);
                return Integer.compare(empruntesB, empruntesA);
            });

            return resultats.subList(0, Math.min(topN, resultats.size()));

        } catch (Exception e) {
            System.err.println("❌ Erreur XQuery top livres: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * XQuery: Statistiques - Nombre de livres par genre
     */
    public static Map<String, Integer> xqueryStatistiquesParGenre(String filePath) {
        Map<String, Integer> stats = new HashMap<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            String genresQuery = "//Livre/Genre";
            NodeList genreNodes = (NodeList) xpath.compile(genresQuery).evaluate(doc, XPathConstants.NODESET);

            Set<String> genresUniques = new HashSet<>();
            for (int i = 0; i < genreNodes.getLength(); i++) {
                String genre = genreNodes.item(i).getTextContent();
                if (genre != null && !genre.isEmpty()) {
                    genresUniques.add(genre);
                }
            }

            for (String genre : genresUniques) {
                String countQuery = String.format("count(//Livre[Genre='%s'])", genre);
                Double count = (Double) xpath.compile(countQuery).evaluate(doc, XPathConstants.NUMBER);
                stats.put(genre, count.intValue());
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur XQuery statistiques: " + e.getMessage());
        }

        return stats;
    }

    private static int calculerCopiesEmpruntees(Map<String, String> livre) {
        try {
            int total = Integer.parseInt(livre.getOrDefault("NombreCopies", "0"));
            int disponibles = Integer.parseInt(livre.getOrDefault("CopiesDisponibles", "0"));
            return total - disponibles;
        } catch (Exception e) {
            return 0;
        }
    }

    // =============== UTILITAIRES ===============

    private static Map<String, String> extraireDonneesLivre(Element element) {
        Map<String, String> livre = new HashMap<>();

        livre.put("id", element.getAttribute("id"));

        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                String tagName = childElement.getTagName();
                String textContent = childElement.getTextContent();
                livre.put(tagName, textContent);
            }
        }

        return livre;
    }

    public static void afficherResultats(List<Map<String, String>> resultats) {
        System.out.println("\n📊 ===== RÉSULTATS =====");
        System.out.println("Nombre de résultats: " + resultats.size());
        System.out.println("========================\n");

        for (int i = 0; i < resultats.size(); i++) {
            Map<String, String> livre = resultats.get(i);
            System.out.println("📖 Livre " + (i + 1) + ":");
            System.out.println("   ID: " + livre.get("id"));
            System.out.println("   Titre: " + livre.get("Titre"));
            System.out.println("   Auteur: " + livre.get("Auteur"));
            System.out.println("   Année: " + livre.get("Annee"));
            System.out.println("   Copies: " + livre.get("NombreCopies") +
                    " | Disponibles: " + livre.get("CopiesDisponibles"));
            System.out.println("   Genre: " + livre.get("Genre"));
            System.out.println("   Langue: " + livre.get("Langue"));
            System.out.println();
        }
    }

    public static void exporterResultatsVersXML(List<Map<String, String>> resultats, String outputPath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("Resultats");
            doc.appendChild(root);

            for (Map<String, String> livre : resultats) {
                Element livreElement = doc.createElement("Livre");
                livreElement.setAttribute("id", livre.getOrDefault("id", ""));

                for (Map.Entry<String, String> entry : livre.entrySet()) {
                    if (!entry.getKey().equals("id")) {
                        Element field = doc.createElement(entry.getKey());
                        field.setTextContent(entry.getValue());
                        livreElement.appendChild(field);
                    }
                }

                root.appendChild(livreElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(outputPath));
            transformer.transform(source, result);

            System.out.println("✅ Résultats exportés vers: " + outputPath);

        } catch (Exception e) {
            System.err.println("❌ Erreur export XML: " + e.getMessage());
        }
    }

    /**
     * Compter le nombre de livres publiés avant l'an 2000
     */
    public static int compterTotalLivresAvant2000(String filePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            String expression = "count(//Livre[number(Annee) < 2000])";
            Double count = (Double) xpath.compile(expression).evaluate(doc, XPathConstants.NUMBER);
            return count.intValue();
        } catch (Exception e) {
            System.err.println("❌ Erreur XPath comptage avant 2000: " + e.getMessage());
            return 0;
        }
    }

    /**
     * XQuery 1: Afficher tous les titres de livres
     */
    public static List<String> xqueryTousLesTitres(String filePath) {
        try {
            String xpathQuery = "//Livre/Titre/text()";
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            NodeList nodes = (NodeList) xpath.compile(xpathQuery).evaluate(doc, XPathConstants.NODESET);

            List<String> titres = new ArrayList<>();
            for (int i = 0; i < nodes.getLength(); i++) {
                titres.add(nodes.item(i).getTextContent());
            }

            return titres;
        } catch (Exception e) {
            System.err.println("❌ Erreur XQuery titres: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * XQuery 2: Afficher les livres dont le disponible est supérieur à 2
     */
    public static List<Map<String, String>> xqueryLivresDisponiblesSuperieurA2(String filePath) {
        try {
            String xpathQuery = "//Livre[number(CopiesDisponibles) > 2]";
            return executerXPathQuery(filePath, xpathQuery);
        } catch (Exception e) {
            System.err.println(" Erreur XQuery disponibles > 2: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * XQuery 3: Afficher le nombre de livres avant l'an 2000
     */
    public static int xqueryNombreLivresAvant2000(String filePath) {
        try {
            String xpathQuery = "count(//Livre[number(Annee) < 2000])";
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            Double count = (Double) xpath.compile(xpathQuery).evaluate(doc, XPathConstants.NUMBER);
            return count.intValue();
        } catch (Exception e) {
            System.err.println(" Erreur XQuery livres avant 2000: " + e.getMessage());
            return 0;
        }
    }
}