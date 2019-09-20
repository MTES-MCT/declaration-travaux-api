package com.github.mtesmct.rieau.api.infra.persistence.minio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.services.FichierIdService;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.domain.services.FichierServiceException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FichierServiceTests {

    @Autowired
    private FichierService fichierService;
    @Autowired
    private FichierIdService fichierIdService;

    @Test
    public void saveAndFindByIdTest() throws FichierServiceException, IOException {
        FichierId fichierId = this.fichierIdService.creer();
        File file = new File("src/test/fixtures/dummy.pdf");
        FileInputStream fis = new FileInputStream(file);
        Fichier fichier = new Fichier("dummy.pdf", "application/pdf", fis, fis.available());
        this.fichierService.save(fichierId, fichier);
        Optional<Fichier> fichierTrouve = this.fichierService.findById(fichierId);
        assertTrue(fichierTrouve.isPresent());
        assertEquals(fichier, fichierTrouve.get());
        assertEquals(fichier.nom(), fichierTrouve.get().nom());
        assertEquals(fichier.mimeType(), fichierTrouve.get().mimeType());
        assertEquals(fichier.content(), fichierTrouve.get().content());
        fis.close();
    }
    
}