package com.github.mtesmct.rieau.api.infra.application.dossiers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.DossierNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.AjouterPieceJointeException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumStatuts;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.MairieForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceNonAJoindreException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossierNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.User;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.repositories.SaveDossierException;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.domain.services.StatutService;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TxPrendreDecisionDossierServiceTests {
        @Autowired
        private TxPrendreDecisionDossierService service;

        @Autowired
        @Qualifier("deposantBeta")
        private User deposantBeta;
        @Autowired
        @Qualifier("instructeurNonBeta")
        private User instructeur;
        @Autowired
        private DossierFactory dossierFactory;
        @Autowired
        private ProjetFactory projetFactory;
        @Autowired
        private DossierRepository dossierRepository;
        @Autowired
        private FichierService fichierService;
        @Autowired
        private FichierFactory fichierFactory;
        @Autowired
        private StatutService statutService;
        private Dossier dossier;
        private Dossier otherDossier;

        @BeforeEach
        public void setUp() throws StatutForbiddenException, TypeStatutNotFoundException, TypeDossierNotFoundException,
                        FileNotFoundException, CommuneNotFoundException, SaveDossierException {
                Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44",
                                "Cedex 01", new ParcelleCadastrale("0", "1", "2"), true, false);
                File cerfaFile = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
                Fichier cerfaFichier = this.fichierFactory.creer(cerfaFile, "application/pdf");
                this.fichierService.save(cerfaFichier);
                this.dossier = this.dossierFactory.creer(this.deposantBeta, EnumTypes.DPMI, projet,
                                cerfaFichier.identity());
                this.statutService.qualifier(this.dossier);    
                this.statutService.instruire(this.dossier);    
                this.statutService.declarerIncomplet(this.dossier, this.instructeur, "Incomplet!");       
                this.statutService.instruire(this.dossier);    
                this.statutService.declarerComplet(this.dossier);     
                this.statutService.lancerConsultations(this.dossier);             
                this.dossier = this.dossierRepository.save(this.dossier);
                assertEquals(this.dossier.statutActuel().get().type().identity(), EnumStatuts.CONSULTATIONS);
        }

        @Test
        @WithMairieBetaDetails
        public void executeMairieTest() throws IOException, MairieForbiddenException, AuthRequiredException,
                        UserForbiddenException, UserInfoServiceException, CommuneNotFoundException,
                        StatutForbiddenException, TypeStatutNotFoundException, TypeDossierNotFoundException,
                        PieceNonAJoindreException, DossierNotFoundException, AjouterPieceJointeException,
                        SaveDossierException {
                File file = new File("src/test/fixtures/dummy.pdf");
                Optional<Dossier> dossier = this.service.execute(this.dossier.identity(),
                                new FileInputStream(file), file.getName(), "application/pdf", file.length());
                assertTrue(dossier.isPresent());
                assertEquals(EnumStatuts.DECISION, dossier.get().statutActuel().get().type().identity());
                assertNotNull(dossier.get().decision());
        }

        @Test
        @WithMairieBetaDetails
        public void executeAutreMairieInterditTest()
                        throws IOException, AjouterPieceJointeException, AuthRequiredException, UserForbiddenException,
                        UserInfoServiceException, CommuneNotFoundException, StatutForbiddenException,
                        TypeStatutNotFoundException, TypeDossierNotFoundException, PieceNonAJoindreException,
                        DossierNotFoundException, MairieForbiddenException, SaveDossierException {
                Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "75100", "BP 44",
                                "Cedex 01", new ParcelleCadastrale("0", "1", "2"), true, false);
                File cerfaFile = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
                Fichier cerfaFichier = this.fichierFactory.creer(cerfaFile, "application/pdf");
                this.fichierService.save(cerfaFichier);
                this.otherDossier = this.dossierFactory.creer(this.deposantBeta, EnumTypes.DPMI, projet,
                                cerfaFichier.identity());
                this.statutService.qualifier(this.otherDossier);    
                this.statutService.instruire(this.otherDossier);    
                this.statutService.declarerIncomplet(this.otherDossier, this.instructeur, "Incomplet!");       
                this.statutService.instruire(this.otherDossier);    
                this.statutService.declarerComplet(this.otherDossier);     
                this.statutService.lancerConsultations(this.otherDossier);             
                this.otherDossier = this.dossierRepository.save(this.otherDossier);
                assertEquals(EnumStatuts.CONSULTATIONS, this.otherDossier.statutActuel().get().type().identity());
                File file = new File("src/test/fixtures/dummy.pdf");
                assertThrows(MairieForbiddenException.class, () -> this.service.execute(this.otherDossier.identity(),
                                new FileInputStream(file), file.getName(), "application/pdf", file.length()));
        }

        @Test
        @WithInstructeurNonBetaDetails
        public void executeInstructeurInterditTest() throws IOException, AjouterPieceJointeException, AuthRequiredException,
                        UserForbiddenException, UserInfoServiceException, CommuneNotFoundException {
                File file = new File("src/test/fixtures/dummy.pdf");
                assertThrows(UserForbiddenException.class, () -> this.service.execute(this.dossier.identity(),
                                new FileInputStream(file), file.getName(), "application/pdf", file.length()));

        }
}