package com.exapmle.hadeellina;

import com.example.tp1gd.models.Client;
import com.example.tp1gd.models.Emprunt;
import com.example.tp1gd.models.Livre;
import org.junit.jupiter.api.Test;

class ApplicationStateTest {

    @Test
    void testIntegrationClientLivreEmprunt() {
        // Création des objets basés sur vos données XML
        Client client = new Client("CLIENT_1762962389059", "gouni", "hadil",
                "0791561112", "el alia", "gounihadilasma@gmail.com", "123456");

        Livre livre = new Livre("L_1762952865264_2",
                "Harry Potter and the Chamber of Secrets",
                "J.K. Rowling", "2003", 3);
        livre.setCopiesDisponibles(2);

        Emprunt emprunt = new Emprunt("L_1762952865264_2", "CLIENT_1762962389059",
                "2025-11-12", "2025-11-15");

        // Vérifications d'intégration
        assertEquals(client.getId(), emprunt.getIdClient());
        assertEquals(livre.getId(), emprunt.getIdLivre());
        assertEquals(1, livre.getCopiesEmpruntees()); // 3 total - 2 disponible = 1 emprunté

        assertTrue(livre.estCoherent());
        assertEquals("GOUNI Hadil", client.getNomComplet()); // CORRECTION ICI
        assertFalse(emprunt.estEnRetard()); // Date dans le futur
    }

    @Test
    void testCohérenceDonneesMultiples() {
        // Simulation de plusieurs emprunts pour le même livre
        Livre livre = new Livre("L_1762952865269_4",
                "Harry Potter Boxed Set  Books 1-5",
                "J.K. Rowling/Mary GrandPré", "2004", 2);
        livre.setCopiesDisponibles(0);

        // Deux emprunts pour le même livre (comme dans vos données)
        Emprunt emprunt1 = new Emprunt("L_1762952865269_4", "CLIENT_1762962389059",
                "2025-11-14", "2025-11-17");
        Emprunt emprunt2 = new Emprunt("L_1762952865269_4", "CLIENT_1763063707473",
                "2025-11-14", "2025-11-17");

        // Vérification que le calcul des copies empruntées est correct
        assertEquals(2, livre.getCopiesEmpruntees()); // 2 total - 0 disponible = 2 empruntés
        assertTrue(livre.estCoherent());

        // Vérification que les emprunts concernent le même livre mais des clients différents
        assertEquals(emprunt1.getIdLivre(), emprunt2.getIdLivre());
        assertNotEquals(emprunt1.getIdClient(), emprunt2.getIdClient());
    }
}