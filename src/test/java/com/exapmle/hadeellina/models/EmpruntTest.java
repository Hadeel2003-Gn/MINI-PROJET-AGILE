package com.exapmle.hadeellina.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmpruntTest {

    private Emprunt emprunt1; // Gouni - livre L_1762952865264_2
    private Emprunt emprunt2; // Gouni - livre L_1762952865269_4
    private Emprunt emprunt3; // Lamri - livre L_1762952865233_0

    @BeforeEach
    void setUp() {
        emprunt1 = new Emprunt("L_1762952865264_2", "CLIENT_1762962389059", "2025-11-12", "2025-11-15");
        emprunt2 = new Emprunt("L_1762952865269_4", "CLIENT_1762962389059", "2025-11-14", "2025-11-17");
        emprunt3 = new Emprunt("L_1762952865233_0", "CLIENT_1763063707473", "2025-11-14", "2025-11-17");
    }

    @Test
    void testGetters_AvecVosDonnees() {
        assertEquals("L_1762952865264_2", emprunt1.getIdLivre());
        assertEquals("CLIENT_1762962389059", emprunt1.getIdClient());
        assertEquals("2025-11-12", emprunt1.getDate());
        assertEquals("2025-11-15", emprunt1.getDateRetour());

        assertEquals("L_1762952865233_0", emprunt3.getIdLivre());
        assertEquals("CLIENT_1763063707473", emprunt3.getIdClient());
    }

    @Test
    void testEstEnRetard_PourDatesFutures() {
        // Tous les emprunts ont des dates dans le futur (2025), donc ne doivent pas être en retard
        assertFalse(emprunt1.estEnRetard());
        assertFalse(emprunt2.estEnRetard());
        assertFalse(emprunt3.estEnRetard());
    }

    @Test
    void testGetJoursRetard_PourDatesFutures() {
        // Pour des dates futures, le retard doit être 0
        assertEquals(0, emprunt1.getJoursRetard());
        assertEquals(0, emprunt2.getJoursRetard());
        assertEquals(0, emprunt3.getJoursRetard());
    }

    @Test
    void testCalculDateRetour_Automatique() {
        // Test du constructeur qui calcule automatiquement la date de retour
        Emprunt empruntAuto = new Emprunt("L_TEST", "CLIENT_TEST", "2025-01-10");

        // La date de retour doit être 3 jours après la date d'emprunt
        assertEquals("2025-01-13", empruntAuto.getDateRetour());
    }

    @Test
    void testEmpruntsMemeClient() {
        // Vérifie qu'un client a plusieurs emprunts
        assertEquals("CLIENT_1762962389059", emprunt1.getIdClient());
        assertEquals("CLIENT_1762962389059", emprunt2.getIdClient());
    }

    @Test
    void testFormatDates() {
        // Vérifie que toutes les dates sont au format correct (yyyy-MM-dd)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        assertDoesNotThrow(() -> LocalDate.parse(emprunt1.getDate(), formatter));
        assertDoesNotThrow(() -> LocalDate.parse(emprunt1.getDateRetour(), formatter));
        assertDoesNotThrow(() -> LocalDate.parse(emprunt2.getDate(), formatter));
    }

    @Test
    void testEmpruntAvecRetard() {
        // Créer un emprunt avec une date de retour dans le passé pour tester le retard
        String datePassee = LocalDate.now().minusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE);
        Emprunt empruntEnRetard = new Emprunt("L_TEST", "CLIENT_TEST", "2024-01-01", datePassee);

        assertTrue(empruntEnRetard.estEnRetard());
        assertTrue(empruntEnRetard.getJoursRetard() > 0);
    }
}