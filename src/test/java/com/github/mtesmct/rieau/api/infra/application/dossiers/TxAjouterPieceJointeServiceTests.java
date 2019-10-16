package com.github.mtesmct.rieau.api.infra.application.dossiers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.AjouterPieceJointeException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumStatuts;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceNonAJoindreException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossierNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithMairieBetaDetails;

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
public class TxAjouterPieceJointeServiceTests {
        @Autowired
        private TxAjouterPieceJointeService ajouterPieceJointe;

        @Autowired
        @Qualifier("deposantBeta")
        private Personne deposantBeta;
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

        @Test
        @WithDeposantBetaDetails
        public void executeDP1Test()
                        throws IOException, AjouterPieceJointeException, AuthRequiredException, UserForbiddenException,
                        UserInfoServiceException, CommuneNotFoundException, StatutForbiddenException,
                        TypeStatutNotFoundException, TypeDossierNotFoundException, PieceNonAJoindreException {
                Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44",
                                "Cedex 01", new ParcelleCadastrale("0", "1", "2"), true, false);
                File cerfaFile = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
                Fichier cerfaFichier = this.fichierFactory.creer(cerfaFile, "application/pdf");
                this.fichierService.save(cerfaFichier);
                Dossier dp = this.dossierFactory.creer(this.deposantBeta, EnumTypes.DPMI, projet,
                                cerfaFichier.identity());
                dp = this.dossierRepository.save(dp);
                assertEquals(dp.statutActuel().get().type().statut(), EnumStatuts.DEPOSE);
                File file = new File("src/test/fixtures/dummy.pdf");
                Optional<PieceJointe> pieceJointe = this.ajouterPieceJointe.execute(dp.identity(), "1",
                                new FileInputStream(file), file.getName(), "application/pdf", file.length());
                assertTrue(pieceJointe.isPresent());
                assertEquals(pieceJointe.get().code().type(), EnumTypes.DPMI);
        }

        @Test
        @WithDeposantBetaDetails
        public void executePCMI1Test()
                        throws IOException, AjouterPieceJointeException, AuthRequiredException, UserForbiddenException,
                        UserInfoServiceException, CommuneNotFoundException, StatutForbiddenException,
                        TypeStatutNotFoundException, TypeDossierNotFoundException, PieceNonAJoindreException {
                Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44",
                                "Cedex 01", new ParcelleCadastrale("0", "1", "2"), true, true);
                File cerfaFile = new File("src/test/fixtures/cerfa_13406_PCMI.pdf");
                Fichier cerfaFichier = this.fichierFactory.creer(cerfaFile, "application/pdf");
                this.fichierService.save(cerfaFichier);
                Dossier pcmi = this.dossierFactory.creer(this.deposantBeta, EnumTypes.PCMI, projet,
                                cerfaFichier.identity());
                pcmi = this.dossierRepository.save(pcmi);
                Optional<Dossier> optionalDossier = this.dossierRepository.findById(pcmi.identity().toString());
                assertTrue(optionalDossier.isPresent());
                pcmi = optionalDossier.get();
                assertTrue(pcmi.statutActuel().isPresent());
                assertEquals(pcmi.statutActuel().get().type().statut(), EnumStatuts.DEPOSE);
                File file = new File("src/test/fixtures/dummy.pdf");
                Optional<PieceJointe> pieceJointe = this.ajouterPieceJointe.execute(pcmi.identity(), "1",
                                new FileInputStream(file), file.getName(), "application/pdf", file.length());
                assertTrue(pieceJointe.isPresent());
                assertEquals(pieceJointe.get().code().type(), EnumTypes.PCMI);
        }

        @Test
        @WithMairieBetaDetails
        public void executeMairieInterditTest() throws IOException, AjouterPieceJointeException, AuthRequiredException,
                        UserForbiddenException, UserInfoServiceException, CommuneNotFoundException {
                File file = new File("src/test/fixtures/dummy.pdf");
                assertThrows(UserForbiddenException.class, () -> this.ajouterPieceJointe.execute(new DossierId("none"),
                                "1", new FileInputStream(file), file.getName(), "application/pdf", file.length()));

        }
}