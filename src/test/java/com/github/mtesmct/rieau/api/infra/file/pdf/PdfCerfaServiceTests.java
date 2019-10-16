package com.github.mtesmct.rieau.api.infra.file.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Map;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.dossiers.CerfaImportException;
import com.github.mtesmct.rieau.api.application.dossiers.NombrePagesCerfaException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.services.FichierService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PdfCerfaServiceTests {
    @Autowired
    private PdfCerfaImportService filePdfCerfaService;
    @Autowired
    private FichierService fichierService;
    @Autowired
    private FichierFactory fichierFactory;

    @Test
    public void lireCerfaDPMITest() throws Exception {
        File file = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
        Fichier fichier = this.fichierFactory.creer(file, "application/pdf");
        this.fichierService.save(fichier);
        Optional<Fichier> fichierLu = this.fichierService.findById(fichier.identity());
        assertTrue(fichierLu.isPresent());
        Map<String, String> valeurs = this.filePdfCerfaService.lire(fichierLu.get());
        assertEquals(12, valeurs.size());
        assertEquals(EnumTypes.DPMI.toString(), valeurs.get("type"));
        assertEquals("false", valeurs.get("nouvelleConstruction"));
        assertEquals("1", valeurs.get("numeroVoie"));
        assertEquals("Route de Kerrivaud", valeurs.get("voie"));
        assertEquals("", valeurs.get("lieuDit"));
        assertEquals("44500", valeurs.get("codePostal"));
        assertEquals("", valeurs.get("bp"));
        assertEquals("000", valeurs.get("prefixe"));
        assertEquals("CT", valeurs.get("section"));
        assertEquals("0099", valeurs.get("numeroCadastre"));
        fichierLu.get().fermer();
    }
    @Test
    public void lireCerfaPCMITest() throws Exception {
        File file = new File("src/test/fixtures/cerfa_13406_PCMI.pdf");
        Fichier fichier = this.fichierFactory.creer(file, "application/pdf");
        this.fichierService.save(fichier);
        Optional<Fichier> fichierLu = this.fichierService.findById(fichier.identity());
        assertTrue(fichierLu.isPresent());
        Map<String, String> valeurs = this.filePdfCerfaService.lire(fichierLu.get());
        assertEquals(12, valeurs.size());
        assertEquals(EnumTypes.PCMI.toString(), valeurs.get("type"));
        assertEquals("true", valeurs.get("nouvelleConstruction"));
        assertEquals("1", valeurs.get("numeroVoie"));
        assertEquals("Rue des Dervallières", valeurs.get("voie"));
        assertEquals("", valeurs.get("lieuDit"));
        assertEquals("44100", valeurs.get("codePostal"));
        assertEquals("", valeurs.get("bp"));
        assertEquals("000", valeurs.get("prefixe"));
        assertEquals("LV", valeurs.get("section"));
        assertEquals("0040", valeurs.get("numeroCadastre"));
        fichierLu.get().fermer();
    }
    @Test
    public void lireDummyPdfTest() throws Exception {
        File file = new File("src/test/fixtures/dummy.pdf");
        Fichier fichier = this.fichierFactory.creer(file, "application/pdf");
        this.fichierService.save(fichier);
        Optional<Fichier> fichierLu = this.fichierService.findById(fichier.identity());
        assertTrue(fichierLu.isPresent());
        CerfaImportException exception = assertThrows(CerfaImportException.class, () -> this.filePdfCerfaService.lire(fichierLu.get()));
        assertTrue(exception.getCause() instanceof NombrePagesCerfaException);
        fichierLu.get().fermer();
    }
}