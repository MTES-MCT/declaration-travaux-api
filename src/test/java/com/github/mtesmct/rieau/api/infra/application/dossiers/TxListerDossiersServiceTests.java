package com.github.mtesmct.rieau.api.infra.application.dossiers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.personnes.User;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithInstructeurNonBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithMairieBetaDetails;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@WithDeposantBetaDetails
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TxListerDossiersServiceTests {
    @Autowired
    private DossierRepository dossierRepository;

    @Autowired
    private TxListerDossiersService listerDossiersService;
    @Autowired
    private DossierFactory dossierFactory;
    @Autowired
    private ProjetFactory projetFactory;

    private Dossier dossier;
    @Autowired
    @Qualifier("deposantBeta")
    private User deposantBeta;
    @Autowired
    private FichierService fichierService;
    @Autowired
    private FichierFactory fichierFactory;

    @BeforeEach
    public void setUp() throws Exception {
        Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01",
                new ParcelleCadastrale("0", "1", "2"), true, true);
        File cerfaFile = new File("src/test/fixtures/dummy.pdf");
        Fichier cerfaFichier = this.fichierFactory.creer(cerfaFile, "application/pdf");
        this.fichierService.save(cerfaFichier);
        this.dossier = this.dossierFactory.creer(this.deposantBeta, EnumTypes.DPMI, projet, cerfaFichier.identity());
        this.dossierRepository.save(this.dossier);
        assertTrue(this.dossier.statutActuel().isPresent());
    }

    @Test
    @WithDeposantBetaDetails
    public void executeDeposantTest() throws AuthRequiredException, UserForbiddenException, UserInfoServiceException {
        assertNotNull(this.listerDossiersService);
        assertFalse(this.listerDossiersService.execute().isEmpty());
        assertEquals(this.listerDossiersService.execute().size(), 1);
        assertNotNull(this.listerDossiersService.execute().get(0));
        assertEquals(this.listerDossiersService.execute().get(0).identity(), this.dossier.identity());
        assertEquals(this.listerDossiersService.execute().get(0).statutActuel(), this.dossier.statutActuel());
        assertEquals(0, this.listerDossiersService.execute().get(0).statutActuel().get().dateDebut()
                .compareTo(this.dossier.statutActuel().get().dateDebut()));
    }

    @Test
    @WithMairieBetaDetails
    public void executeMairieTest() throws AuthRequiredException, UserForbiddenException, UserInfoServiceException {
        assertNotNull(this.listerDossiersService);
        assertFalse(this.listerDossiersService.execute().isEmpty());
        assertEquals(this.listerDossiersService.execute().size(), 1);
        assertNotNull(this.listerDossiersService.execute().get(0));
        assertEquals(this.listerDossiersService.execute().get(0).identity(), this.dossier.identity());
        assertTrue(this.listerDossiersService.execute().get(0).statutActuel().isPresent());
        assertEquals(this.listerDossiersService.execute().get(0).statutActuel(), this.dossier.statutActuel());
        assertEquals(0, this.listerDossiersService.execute().get(0).statutActuel().get().dateDebut()
                .compareTo(this.dossier.statutActuel().get().dateDebut()));
    }

    @Test
    @WithInstructeurNonBetaDetails
    public void executeInstructeurTest() throws AuthRequiredException, UserForbiddenException, UserInfoServiceException {
        assertNotNull(this.listerDossiersService);
        assertFalse(this.listerDossiersService.execute().isEmpty());
        assertEquals(this.listerDossiersService.execute().size(), 1);
        assertNotNull(this.listerDossiersService.execute().get(0));
        assertEquals(this.listerDossiersService.execute().get(0).identity(), this.dossier.identity());
        assertTrue(this.listerDossiersService.execute().get(0).statutActuel().isPresent());
        assertEquals(this.listerDossiersService.execute().get(0).statutActuel(), this.dossier.statutActuel());
        assertEquals(0, this.listerDossiersService.execute().get(0).statutActuel().get().dateDebut()
                .compareTo(this.dossier.statutActuel().get().dateDebut()));
    }
}