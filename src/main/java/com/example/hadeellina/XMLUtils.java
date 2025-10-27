package com.example.hadeellina;

import com.example.hadeellina.models.Client;
import com.example.hadeellina.models.Emprunt;
import com.example.hadeellina.models.Livre;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLUtils {

    // ======= LIVRES =======
    public static List<Livre> lireLivres(String filePath) throws Exception {
        List<Livre> livres = new ArrayList<>();
        File f = new File(filePath);
        if (!f.exists()) {
            System.out.println("⚠️ Fichier " + filePath + " non trouvé, création d'une nouvelle liste");
            return livres;
        }

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(f);
        NodeList nodes = doc.getElementsByTagName("Livre");

        System.out.println("📖 Lecture de " + nodes.getLength() + " livres depuis XML...");

        for (int i = 0; i < nodes.getLength(); i++) {
            Element el = (Element) nodes.item(i);
            String id = el.getAttribute("id");
            String titre = el.getElementsByTagName("Titre").item(0).getTextContent();
            String auteur = el.getElementsByTagName("Auteur").item(0).getTextContent();
            String annee = el.getElementsByTagName("Annee").item(0).getTextContent();

            int nombreCopies = 1;
            int copiesDisponibles = 1;

            // Lire nombreCopies s'il existe
            NodeList nombreCopiesNodes = el.getElementsByTagName("NombreCopies");
            if (nombreCopiesNodes.getLength() > 0) {
                try {
                    nombreCopies = Integer.parseInt(nombreCopiesNodes.item(0).getTextContent());
                } catch (NumberFormatException e) {
                    nombreCopies = 1;
                }
            }

            // Lire copiesDisponibles s'il existe
            NodeList copiesDisponiblesNodes = el.getElementsByTagName("CopiesDisponibles");
            if (copiesDisponiblesNodes.getLength() > 0) {
                try {
                    copiesDisponibles = Integer.parseInt(copiesDisponiblesNodes.item(0).getTextContent());
                } catch (NumberFormatException e) {
                    copiesDisponibles = nombreCopies;
                }
            }

            Livre livre = new Livre(id, titre, auteur, annee, nombreCopies);
            livre.setCopiesDisponibles(copiesDisponibles);

            // Lire la propriété optionnelle langue
            livre.setLangue(getElementText(el, "Langue", "Français"));

            // 🔥 CORRECTION : LIRE TOUTES LES INFORMATIONS PERSONNALISÉES Y COMPRIS ISBN, GENRE, ÉDITEUR
            Map<String, String> infosPersonnalisees = new HashMap<>();
            NodeList allChildNodes = el.getChildNodes();

            for (int j = 0; j < allChildNodes.getLength(); j++) {
                Node node = allChildNodes.item(j);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element childEl = (Element) node;
                    String tagName = childEl.getTagName();
                    String valeur = childEl.getTextContent();

                    // 🔥 CORRECTION : Ignorer uniquement les balises standard principales
                    if (!tagName.equals("Titre") &&
                            !tagName.equals("Auteur") &&
                            !tagName.equals("Annee") &&
                            !tagName.equals("NombreCopies") &&
                            !tagName.equals("CopiesDisponibles") &&
                            !tagName.equals("Langue")) {

                        if (valeur != null && !valeur.trim().isEmpty()) {
                            infosPersonnalisees.put(tagName, valeur.trim());
                            System.out.println("   📋 " + tagName + ": " + valeur);
                        }
                    }
                }
            }

            livre.setInformationsPersonnalisees(infosPersonnalisees);
            livres.add(livre);
        }

        System.out.println("✅ " + livres.size() + " livres chargés depuis XML");
        return livres;
    }

    public static void saveLivres(List<Livre> livres, String filePath) throws Exception {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.newDocument();
        Element root = doc.createElement("Livres");
        doc.appendChild(root);

        System.out.println("💾 Sauvegarde de " + livres.size() + " livres dans XML...");

        for (Livre l : livres) {
            Element livreEl = doc.createElement("Livre");
            livreEl.setAttribute("id", l.getId());

            // Informations de base
            createAndAppendElement(doc, livreEl, "Titre", l.getTitre());
            createAndAppendElement(doc, livreEl, "Auteur", l.getAuteur());
            createAndAppendElement(doc, livreEl, "Annee", l.getAnnee());
            createAndAppendElement(doc, livreEl, "NombreCopies", String.valueOf(l.getNombreCopies()));
            createAndAppendElement(doc, livreEl, "CopiesDisponibles", String.valueOf(l.getCopiesDisponibles()));

            // Propriété optionnelle langue
            createAndAppendElement(doc, livreEl, "Langue", l.getLangue());

            // 🔥 CORRECTION : Informations personnalisées dynamiques - TOUTES les balises
            for (Map.Entry<String, String> entry : l.getInformationsPersonnalisees().entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    createAndAppendElement(doc, livreEl, entry.getKey(), entry.getValue());
                    System.out.println("   💾 " + entry.getKey() + ": " + entry.getValue());
                }
            }

            root.appendChild(livreEl);
        }
        saveDocument(doc, filePath);
        System.out.println("✅ Livres sauvegardés avec succès !");
    }

    // ======= CLIENTS =======
    public static List<Client> lireClients(String filePath) throws Exception {
        List<Client> clients = new ArrayList<>();
        File f = new File(filePath);
        if (!f.exists()) {
            System.out.println("⚠️ Fichier " + filePath + " non trouvé, création d'une nouvelle liste");
            return clients;
        }

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(f);
        NodeList nodes = doc.getElementsByTagName("Client");

        for (int i = 0; i < nodes.getLength(); i++) {
            Element el = (Element) nodes.item(i);
            String id = el.getAttribute("id");
            String nom = getElementText(el, "Nom");
            String prenom = getElementText(el, "Prenom");
            String tel = getElementText(el, "Telephone");
            String adresse = getElementText(el, "Adresse");
            String email = getElementText(el, "Email");
            String password = getElementText(el, "Password"); // 🔐 Lecture du mot de passe

            // 🔥 CORRECTION : Utiliser le constructeur avec TOUS les paramètres
            clients.add(new Client(id, nom, prenom, tel, adresse, email, password));
        }

        System.out.println("✅ " + clients.size() + " clients chargés depuis XML");
        return clients;
    }

    public static void saveClients(List<Client> clients, String filePath) throws Exception {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.newDocument();

        Element root = doc.createElement("Clients");
        doc.appendChild(root);

        for (Client c : clients) {
            Element clientEl = doc.createElement("Client");
            clientEl.setAttribute("id", c.getId());

            createAndAppendElement(doc, clientEl, "Nom", c.getNom());
            createAndAppendElement(doc, clientEl, "Prenom", c.getPrenom());
            createAndAppendElement(doc, clientEl, "Telephone", c.getTelephone());
            createAndAppendElement(doc, clientEl, "Adresse", c.getAdresse());
            createAndAppendElement(doc, clientEl, "Email", c.getEmail());
            createAndAppendElement(doc, clientEl, "Password", c.getPassword()); // 🔐 Sauvegarde du mot de passe

            root.appendChild(clientEl);
        }
        saveDocument(doc, filePath);
        System.out.println("✅ Clients sauvegardés avec succès !");
    }

    // ======= EMPRUNTS =======
    public static List<Emprunt> lireEmprunts(String filePath) throws Exception {
        List<Emprunt> emprunts = new ArrayList<>();
        File f = new File(filePath);
        if (!f.exists()) {
            System.out.println("⚠️ Fichier " + filePath + " non trouvé, création d'une nouvelle liste");
            return emprunts;
        }

        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(f);
        NodeList nodes = doc.getElementsByTagName("Emprunt");

        for (int i = 0; i < nodes.getLength(); i++) {
            Element el = (Element) nodes.item(i);
            String idLivre = el.getElementsByTagName("IdLivre").item(0).getTextContent();
            String idClient = el.getElementsByTagName("IdClient").item(0).getTextContent();
            String date = el.getElementsByTagName("Date").item(0).getTextContent();

            // Lire la date de retour si elle existe, sinon la calculer
            String dateRetour;
            NodeList dateRetourNodes = el.getElementsByTagName("DateRetour");
            if (dateRetourNodes.getLength() > 0) {
                dateRetour = dateRetourNodes.item(0).getTextContent();
            } else {
                // Calculer automatiquement la date de retour (3 jours)
                dateRetour = calculerDateRetour(date);
            }

            emprunts.add(new Emprunt(idLivre, idClient, date, dateRetour));
        }

        System.out.println("✅ " + emprunts.size() + " emprunts chargés depuis XML");
        return emprunts;
    }

    // Méthode utilitaire pour calculer la date de retour
    private static String calculerDateRetour(String dateEmprunt) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dateEmprunt, formatter);
            LocalDate dateRetour = date.plusDays(3); // 3 jours de délai
            return dateRetour.format(formatter);
        } catch (Exception e) {
            return LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    }

    public static void saveEmprunts(List<Emprunt> emprunts, String filePath) throws Exception {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.newDocument();
        Element root = doc.createElement("Emprunts");
        doc.appendChild(root);

        for (Emprunt e : emprunts) {
            Element el = doc.createElement("Emprunt");

            createAndAppendElement(doc, el, "IdLivre", e.getIdLivre());
            createAndAppendElement(doc, el, "IdClient", e.getIdClient());
            createAndAppendElement(doc, el, "Date", e.getDate());
            createAndAppendElement(doc, el, "DateRetour", e.getDateRetour());

            root.appendChild(el);
        }
        saveDocument(doc, filePath);
        System.out.println("✅ Emprunts sauvegardés avec succès !");
    }

    // ======= MÉTHODES UTILITAIRES =======
    private static void createAndAppendElement(Document doc, Element parent, String tagName, String textContent) {
        if (textContent != null && !textContent.isEmpty()) {
            Element element = doc.createElement(tagName);
            element.setTextContent(textContent);
            parent.appendChild(element);
        }
    }

    private static String getElementText(Element parent, String tagName) {
        return getElementText(parent, tagName, "");
    }

    private static String getElementText(Element parent, String tagName, String defaultValue) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return defaultValue;
    }

    private static void saveDocument(Document doc, String filePath) throws TransformerException {
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        t.transform(new DOMSource(doc), new StreamResult(new File(filePath)));
    }
}
