package com.github.mtesmct.rieau.api.infra.application.fichiers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.DossierNotFoundException;
import com.github.mtesmct.rieau.api.application.dossiers.FichierNotFoundException;
import com.github.mtesmct.rieau.api.application.dossiers.UserNotOwnerException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.MairieForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.infra.application.auth.WithAutreDeposantBetaDetails;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TxLireFichierTests {
    @Autowired
    private TxLireFichierService lireFichierService;
    @Autowired
    private FichierService fichierService;
    @Autowired
    private FichierFactory fichierFactory;
    @Autowired
    private DossierFactory dossierFactory;
    @Autowired
    private ProjetFactory projetFactory;
    @Autowired
    private DossierRepository dossierRepository;

    private Fichier fichier;
    @Autowired
    @Qualifier("deposantBeta")
    private Personne deposantBeta;

    @BeforeEach
    public void setup() throws IOException, CommuneNotFoundException {
        File file = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
        fichier = this.fichierFactory.creer(file, "application/pdf");
        this.fichierService.save(fichier);
        Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01",
                new ParcelleCadastrale("0", "1", "2"), true, true);
        Dossier dp = this.dossierFactory.creer(this.deposantBeta, TypesDossier.DP, projet);
        dp.ajouterCerfa(fichier.identity());
        dp = this.dossierRepository.save(dp);
    }

    @Test
    @WithDeposantBetaDetails
    public void lireDeposantTest() throws FichierNotFoundException, UserForbiddenException, AuthRequiredException,
            UserInfoServiceException, UserNotOwnerException, DossierNotFoundException {
        Optional<Fichier> fichierLu = this.lireFichierService.execute(fichier.identity());
        assertTrue(fichierLu.isPresent());
    }

    @Test
    @WithMairieBetaDetails
    public void lireMairieTest() throws FichierNotFoundException, UserForbiddenException, AuthRequiredException,
            UserInfoServiceException, UserNotOwnerException, DossierNotFoundException {
        Optional<Fichier> fichierLu = this.lireFichierService.execute(fichier.identity());
        assertTrue(fichierLu.isPresent());
    }

    @Test
    @WithMairieBetaDetails
    public void lireMairieNonLocaliseeTest()
            throws FichierNotFoundException, UserForbiddenException, AuthRequiredException, UserInfoServiceException,
            UserNotOwnerException, DossierNotFoundException, FileNotFoundException, CommuneNotFoundException {
        File file = new File("src/test/fixtures/dummy.pdf");
        Fichier fichier = this.fichierFactory.creer(file, "application/pdf");
        this.fichierService.save(fichier);
        Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44500", "BP 44", "Cedex 01",
                new ParcelleCadastrale("0", "1", "2"), true, true);
        Dossier dp = this.dossierFactory.creer(this.deposantBeta, TypesDossier.DP, projet);
        dp.ajouterCerfa(fichier.identity());
        dp = this.dossierRepository.save(dp);
        MairieForbiddenException exception = assertThrows(MairieForbiddenException.class,
                () -> lireFichierService.execute(fichier.identity()));
        assertNotNull(exception);
    }

    @Test
    public void lireNonAuthentifieTest() throws FichierNotFoundException, UserForbiddenException {
        AuthRequiredException exception = assertThrows(AuthRequiredException.class,
                () -> lireFichierService.execute(fichier.identity()));
        assertNotNull(exception);
    }

    @Test
    @WithInstructeurNonBetaDetails
    public void lireInterditTest() throws FichierNotFoundException, UserForbiddenException {
        UserForbiddenException exception = assertThrows(UserForbiddenException.class,
                () -> lireFichierService.execute(fichier.identity()));
        assertNotNull(exception);
    }

    @Test
    @WithAutreDeposantBetaDetails
    public void lireNonProprietaireTest() throws FichierNotFoundException, UserForbiddenException {
        UserNotOwnerException exception = assertThrows(UserNotOwnerException.class,
                () -> lireFichierService.execute(fichier.identity()));
        assertNotNull(exception);
    }

}