package com.exapmle.hadeellina.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientTest {

    private Client clientGouni;
    private Client clientLamri;
    private Client clientKaci;

    @BeforeEach
    void setUp() {
        clientGouni = new Client("CLIENT_1762962389059", "gouni", "hadil", "0791561112",
                "el alia", "gounihadilasma@gmail.com", "123456");

        clientLamri = new Client("CLIENT_1763063707473", "lamri", "lina", "1234567890",
                "el-alia", "lamrilina044@gmail.com", "1234567");

        clientKaci = new Client("CLIENT_1763119931790", "kaci", "amina", "0799456312",
                "Meftah-Blida", "aminakaci@gmail.com", "12345678");
    }

    @Test
    void testGetNomComplet_AvecVosDonnees() {
        // Vérifier le format réel de votre méthode getNomComplet()
        assertEquals("GOUNI Hadil", clientGouni.getNomComplet());
        assertEquals("LAMRI Lina", clientLamri.getNomComplet());
        assertEquals("KACI Amina", clientKaci.getNomComplet());
    }

    @Test
    void testGetters_AvecVosDonnees() {
        // Test client Gouni
        assertEquals("CLIENT_1762962389059", clientGouni.getId());
        assertEquals("gouni", clientGouni.getNom());
        assertEquals("hadil", clientGouni.getPrenom());
        assertEquals("0791561112", clientGouni.getTelephone());
        assertEquals("el alia", clientGouni.getAdresse());
        assertEquals("gounihadilasma@gmail.com", clientGouni.getEmail());
        assertEquals("123456", clientGouni.getPassword());

        // Test client Lamri
        assertEquals("lamrilina044@gmail.com", clientLamri.getEmail());
        assertEquals("1234567", clientLamri.getPassword());
    }

    @Test
    void testToString_UtiliseNomComplet() {
        assertEquals("GOUNI Hadil", clientGouni.toString());
        assertEquals("LAMRI Lina", clientLamri.toString());
    }

    @Test
    void testValidationEmail() {
        assertTrue(clientGouni.getEmail().contains("@"));
        assertTrue(clientLamri.getEmail().contains("@"));
        assertTrue(clientKaci.getEmail().contains("@"));
    }

    @Test
    void testValidationTelephone() {
        assertTrue(clientGouni.getTelephone().matches("\\d+"));
        assertTrue(clientLamri.getTelephone().matches("\\d+"));
        assertTrue(clientKaci.getTelephone().matches("\\d+"));
    }
}