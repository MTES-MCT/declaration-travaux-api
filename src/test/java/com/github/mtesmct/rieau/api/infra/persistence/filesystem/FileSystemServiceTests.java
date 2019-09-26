package com.github.mtesmct.rieau.api.infra.persistence.filesystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
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
public class FileSystemServiceTests {
    @Autowired
    private FichierService fichierService;
    @Autowired
    private FichierFactory fichierFactory;

    @Test
    public void saveAndFindByIdAndDeleteTest() throws FileNotFoundException {
        File file = new File("src/test/fixtures/dummy.pdf");
        Fichier fichier = this.fichierFactory.creer(file, "application/pdf");
        this.fichierService.save(fichier);
        Optional<Fichier> fichierTrouve = this.fichierService.findById(fichier.identity());
        assertFalse(fichierTrouve.isEmpty());
        assertEquals(fichier, fichierTrouve.get());
        assertEquals(fichier.nom(), fichierTrouve.get().nom());
        assertEquals(fichier.mimeType(), fichierTrouve.get().mimeType());
        this.fichierService.delete(fichier.identity());
        assertTrue(this.fichierService.findById(fichier.identity()).isEmpty());
    }
    
}