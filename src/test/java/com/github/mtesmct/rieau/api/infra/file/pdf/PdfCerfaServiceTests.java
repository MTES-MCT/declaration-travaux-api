package com.github.mtesmct.rieau.api.infra.file.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
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
        Optional<String> code = this.filePdfCerfaService.lireCode(fichierLu.get());
        assertTrue(code.isPresent());
        assertEquals(code.get(), "13703");
        fichierLu.get().fermer();
    }
    @Test
    public void lireCerfaPCMITest() throws Exception {
        File file = new File("src/test/fixtures/cerfa_13406_PCMI.pdf");
        Fichier fichier = this.fichierFactory.creer(file, "application/pdf");
        this.fichierService.save(fichier);
        Optional<Fichier> fichierLu = this.fichierService.findById(fichier.identity());
        assertTrue(fichierLu.isPresent());
        Optional<String> code = this.filePdfCerfaService.lireCode(fichierLu.get());
        assertTrue(code.isPresent());
        assertEquals(code.get(), "13406");
        fichierLu.get().fermer();
    }
}