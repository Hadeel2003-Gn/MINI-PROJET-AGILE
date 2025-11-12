package com.exapmle.hadeellina.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LivreTest {

    private Livre livre1; // Harry Potter and the Half-Blood Prince - 0 disponible
    private Livre livre2; // Harry Potter and the Order of the Phoenix - 2 disponibles
    private Livre livre3; // Harry Potter and the Chamber of Secrets - 2 disponibles sur 3

    @BeforeEach
    void setUp() {
        livre1 = new Livre("L_1762952865233_0",
                "Harry Potter and the Half-Blood Prince",
                "J.K. Rowling/Mary GrandPré", "2006", 1);
        livre1.setCopiesDisponibles(0);
        livre1.setLangue("Anglais");

        livre2 = new Livre("L_1762952865263_1",
                "Harry Potter and the Order of the Phoenix",
                "J.K. Rowling/Mary GrandPré", "2004", 2);
        livre2.setCopiesDisponibles(2);
        livre2.setLangue("Anglais");

        livre3 = new Livre("L_1762952865264_2",
                "Harry Potter and the Chamber of Secrets",
                "J.K. Rowling", "2003", 3);
        livre3.setCopiesDisponibles(2);
        livre3.setLangue("Anglais");
    }

    @Test
    void testIsDisponible_AvecVosDonnees() {
        assertFalse(livre1.isDisponible()); // 0 disponible
        assertTrue(livre2.isDisponible());  // 2 disponibles
        assertTrue(livre3.isDisponible());  // 2 disponibles
    }

    @Test
    void testGetCopiesEmpruntees_AvecVosDonnees() {
        assertEquals(1, livre1.getCopiesEmpruntees()); // 1 total - 0 disponible = 1 emprunté
        assertEquals(0, livre2.getCopiesEmpruntees()); // 2 total - 2 disponible = 0 emprunté
        assertEquals(1, livre3.getCopiesEmpruntees()); // 3 total - 2 disponible = 1 emprunté
    }

    @Test
    void testEstCoherent_AvecVosDonnees() {
        assertTrue(livre1.estCoherent());
        assertTrue(livre2.estCoherent());
        assertTrue(livre3.estCoherent());
    }

    @Test
    void testSetCopiesDisponibles_Validation() {
        Livre livreTest = new Livre("TEST", "Test", "Auteur", "2024", 3);

        // Test valeur négative
        livreTest.setCopiesDisponibles(-1);
        assertEquals(0, livreTest.getCopiesDisponibles());

        // Test valeur supérieure au total
        livreTest.setCopiesDisponibles(5);
        assertEquals(3, livreTest.getCopiesDisponibles());

        // Test valeur valide
        livreTest.setCopiesDisponibles(2);
        assertEquals(2, livreTest.getCopiesDisponibles());
    }

    @Test
    void testInformationsPersonnalisees() {
        Livre livre = new Livre("TEST", "Test", "Auteur", "2024", 1);

        livre.ajouterInformationPersonnalisee("ISBN", "123-456");
        livre.ajouterInformationPersonnalisee("Genre", "Fantasy");

        assertEquals("123-456", livre.getInformationPersonnalisee("ISBN"));
        assertEquals("Fantasy", livre.getInformationPersonnalisee("Genre"));
        assertNull(livre.getInformationPersonnalisee("Inexistant"));
    }
}
