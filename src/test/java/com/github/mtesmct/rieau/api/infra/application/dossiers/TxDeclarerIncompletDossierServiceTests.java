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
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatut;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.repositories.TypeStatutDossierRepository;
import com.github.mtesmct.rieau.api.domain.services.DateService;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithInstructeurNonBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithMairieBetaDetails;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;

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
public class TxDeclarerIncompletDossierServiceTests {
    @MockBean
    private DossierRepository dossierRepository;

    @Autowired
    private TxDeclarerIncompletDossierService incompletDossierService;
    @Autowired
    private DossierFactory dossierFactory;
    @Autowired
    private ProjetFactory projetFactory;

    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter dateConverter;

    private Dossier dossier;

    private Dossier otherDossier;
    @Autowired
    @Qualifier("deposantBeta")
    private Personne deposantBeta;
    @Autowired
    private FichierService fichierService;
    @Autowired
    private FichierFactory fichierFactory;
    @Autowired
    private DateService dateService;
    @Autowired
    private TypeStatutDossierRepository typeStatutDossierRepository;

    @BeforeEach
    public void setUp() throws Exception {
        Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01",
                new ParcelleCadastrale("0", "1", "2"), true, true);
        File cerfaFile = new File("src/test/fixtures/dummy.pdf");
        Fichier cerfaFichier = this.fichierFactory.creer(cerfaFile, "application/pdf");
        this.fichierService.save(cerfaFichier);
        this.dossier = this.dossierFactory.creer(this.deposantBeta, EnumTypes.DPMI, projet, cerfaFichier.identity());
        Mockito.when(this.dossierRepository.save(this.dossier)).thenReturn(this.dossier);
        Optional<TypeStatut> typeStatut = this.typeStatutDossierRepository.findByStatut(EnumStatuts.QUALIFIE);
        assertTrue(typeStatut.isPresent());
        this.dossier.ajouterStatut(this.dateService.now(), typeStatut.get());
        this.dossier = this.dossierRepository.save(this.dossier);
        assertNotNull(this.dossier);
        assertNotNull(this.dossier.identity());
        assertNotNull(this.dossier.deposant());
        assertTrue(this.dossier.pieceJointes().isEmpty());
        assertTrue(this.dossier.statutActuel().isPresent());
        assertEquals(EnumStatuts.QUALIFIE, this.dossier.statutActuel().get().type().statut());
        Projet otherProjet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "75400", "BP 44",
                "Cedex 01", new ParcelleCadastrale("0", "1", "2"), true, true);
        this.otherDossier = this.dossierFactory.creer(this.deposantBeta, EnumTypes.DPMI, otherProjet, cerfaFichier.identity());
        Mockito.when(this.dossierRepository.save(this.otherDossier)).thenReturn(this.otherDossier);
        this.otherDossier.ajouterStatut(this.dateService.now(), typeStatut.get());
        this.otherDossier = this.dossierRepository.save(this.otherDossier);
        assertNotNull(this.otherDossier);
        assertNotNull(this.otherDossier.identity());
        assertNotNull(this.otherDossier.deposant());
        assertTrue(this.otherDossier.statutActuel().isPresent());
        assertEquals(EnumStatuts.QUALIFIE, this.otherDossier.statutActuel().get().type().statut());
    }

    @Test
    @WithInstructeurNonBetaDetails
    public void executeInstructeurTest() throws AuthRequiredException, UserForbiddenException, UserInfoServiceException,
            InstructeurForbiddenException, DossierNotFoundException, TypeStatutNotFoundException, StatutForbiddenException {
        Mockito.when(this.dossierRepository.findById(anyString())).thenReturn(Optional.ofNullable(this.dossier));
        Optional<Dossier> dossierIncomplet = this.incompletDossierService.execute(this.dossier.identity());
        assertTrue(dossierIncomplet.isPresent());
        assertEquals(this.dossier.identity(), dossierIncomplet.get().identity());
        assertTrue(dossierIncomplet.get().statutActuel().isPresent());
        assertEquals(EnumStatuts.INCOMPLET, dossierIncomplet.get().statutActuel().get().type().statut());
    }

    @Test
    @WithInstructeurNonBetaDetails
    public void executeAutreInstructeurInterditTest() throws AuthRequiredException, UserForbiddenException,
            UserInfoServiceException, InstructeurForbiddenException, DossierNotFoundException {
        Mockito.when(this.dossierRepository.findById(anyString())).thenReturn(Optional.ofNullable(this.otherDossier));
        assertThrows(InstructeurForbiddenException.class,
                () -> this.incompletDossierService.execute(this.otherDossier.identity()));
    }

    @Test
    @WithDeposantBetaDetails
    public void executeDeposantInterditTest() throws Exception {
        Mockito.when(this.dossierRepository.findById(anyString())).thenReturn(Optional.ofNullable(this.dossier));
        assertThrows(UserForbiddenException.class, () -> this.incompletDossierService.execute(this.dossier.identity()));
    }

    @Test
    @WithMairieBetaDetails
    public void executeMairieInterditTest() throws Exception {
        Mockito.when(this.dossierRepository.findById(anyString())).thenReturn(Optional.ofNullable(this.dossier));
        assertThrows(UserForbiddenException.class, () -> this.incompletDossierService.execute(this.dossier.identity()));
    }
}