package com.github.mtesmct.rieau.api.infra.application.dossiers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;

import java.io.File;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.DossierNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumStatuts;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.InstructeurForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.User;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.repositories.SaveDossierException;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.domain.services.StatutService;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithInstructeurNonBetaDetails;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TxLancerConsultationsDossierServiceTests {
    @MockBean
    private DossierRepository dossierRepository;

    @Autowired
    private TxLancerConsultationsDossierService service;
    @Autowired
    private DossierFactory dossierFactory;
    @Autowired
    private ProjetFactory projetFactory;

    private Dossier dossier;

    private Dossier otherDossier;
    @Autowired
    @Qualifier("deposantBeta")
    private User deposantBeta;
    @Autowired
    private FichierService fichierService;
    @Autowired
    private FichierFactory fichierFactory;
    @Autowired
    private StatutService statutService;
    @Autowired
    @Qualifier("instructeurNonBeta")
    private User instructeur;

    @BeforeEach
    public void setUp() throws Exception {
        Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01",
                new ParcelleCadastrale("0", "1", "2"), true, true);
        File cerfaFile = new File("src/test/fixtures/dummy.pdf");
        Fichier cerfaFichier = this.fichierFactory.creer(cerfaFile, "application/pdf");
        this.fichierService.save(cerfaFichier);
        this.dossier = this.dossierFactory.creer(this.deposantBeta, EnumTypes.DPMI, projet, cerfaFichier.identity());
        Mockito.when(this.dossierRepository.save(this.dossier)).thenReturn(this.dossier);
        this.statutService.qualifier(this.dossier);
        this.statutService.instruire(this.dossier);
        this.statutService.declarerIncomplet(this.dossier, this.instructeur, "Incomplet!");
        this.statutService.instruire(this.dossier);
        this.statutService.declarerComplet(this.dossier);
        this.dossier = this.dossierRepository.save(this.dossier);
        assertNotNull(this.dossier);
        assertNotNull(this.dossier.identity());
        assertNotNull(this.dossier.deposant());
        assertTrue(this.dossier.pieceJointes().isEmpty());
        Projet otherProjet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "75400", "BP 44",
                "Cedex 01", new ParcelleCadastrale("0", "1", "2"), true, true);
        this.otherDossier = this.dossierFactory.creer(this.deposantBeta, EnumTypes.DPMI, otherProjet,
                cerfaFichier.identity());
        Mockito.when(this.dossierRepository.save(this.otherDossier)).thenReturn(this.otherDossier);
        this.statutService.qualifier(this.otherDossier);
        this.statutService.instruire(this.otherDossier);
        this.statutService.declarerIncomplet(this.otherDossier, this.instructeur, "Incomplet!");
        this.statutService.instruire(this.otherDossier);
        this.statutService.declarerComplet(this.otherDossier);
        this.otherDossier = this.dossierRepository.save(this.otherDossier);
        assertNotNull(this.otherDossier);
        assertNotNull(this.otherDossier.identity());
        assertNotNull(this.otherDossier.deposant());
    }

    @Test
    @WithInstructeurNonBetaDetails
    public void executeInstructeurTest() throws AuthRequiredException, UserForbiddenException, UserInfoServiceException,
            InstructeurForbiddenException, DossierNotFoundException, TypeStatutNotFoundException,
            StatutForbiddenException, SaveDossierException {
        Mockito.when(this.dossierRepository.findById(anyString())).thenReturn(Optional.ofNullable(this.dossier));
        Optional<Dossier> dossierConsulte = this.service.execute(this.dossier.identity());
        assertTrue(dossierConsulte.isPresent());
        assertEquals(this.dossier.identity(), dossierConsulte.get().identity());
        assertTrue(dossierConsulte.get().statutActuel().isPresent());
        assertEquals(EnumStatuts.CONSULTATIONS, dossierConsulte.get().statutActuel().get().type().identity());
    }

    @Test
    @WithInstructeurNonBetaDetails
    public void executeAutreInstructeurInterditTest() throws AuthRequiredException, UserForbiddenException,
            UserInfoServiceException, InstructeurForbiddenException, DossierNotFoundException {
        Mockito.when(this.dossierRepository.findById(anyString())).thenReturn(Optional.ofNullable(this.otherDossier));
        assertThrows(InstructeurForbiddenException.class, () -> this.service.execute(this.otherDossier.identity()));
    }

    @Test
    @WithDeposantBetaDetails
    public void executeDeposantInterditTest() throws Exception {
        Mockito.when(this.dossierRepository.findById(anyString())).thenReturn(Optional.ofNullable(this.dossier));
        assertThrows(UserForbiddenException.class, () -> this.service.execute(this.dossier.identity()));
    }
}