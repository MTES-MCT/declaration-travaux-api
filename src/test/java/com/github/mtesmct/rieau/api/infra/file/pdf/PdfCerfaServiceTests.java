package com.github.mtesmct.rieau.api.infra.file.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierIdService;
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
    private FichierIdService fichierIdService;

    @Test
    public void lireCerfaDPMITest() throws Exception {
        Fichier fichier = new Fichier("cerfa_13703_DPMI.pdf", "application/pdf", new FileInputStream(new File("src/test/fixtures/cerfa_13703_DPMI.pdf")));
        FichierId fichierId = this.fichierIdService.creer();
        this.fichierService.save(fichierId, fichier);
        Optional<String> code = this.filePdfCerfaService.lireCode(fichier);
        assertTrue(code.isPresent());
        assertEquals(code.get(), "13703");
    }
    @Test
    public void lireCerfaPCMITest() throws Exception {
        Fichier fichier = new Fichier("cerfa_13406_PCMI.pdf", "application/pdf", new FileInputStream(new File("src/test/fixtures/cerfa_13406_PCMI.pdf")));
        FichierId fichierId = this.fichierIdService.creer();
        this.fichierService.save(fichierId, fichier);
        Optional<String> code = this.filePdfCerfaService.lireCode(fichier);
        assertTrue(code.isPresent());
        assertEquals(code.get(), "13406");
    }
}