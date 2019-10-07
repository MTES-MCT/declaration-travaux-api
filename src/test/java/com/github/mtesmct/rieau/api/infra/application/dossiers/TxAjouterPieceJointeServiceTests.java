package com.github.mtesmct.rieau.api.infra.application.dossiers;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantBetaDetails;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WithDeposantBetaDetails
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

    @Test
    @WithDeposantBetaDetails
    public void executeDP1Test() throws IOException, AjouterPieceJointeException, AuthRequiredException,
            UserForbiddenException, UserInfoServiceException, CommuneNotFoundException {
        Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01",
                new ParcelleCadastrale("0", "1", "2"), true);
        Dossier dp = this.dossierFactory.creer(this.deposantBeta, TypesDossier.DP, projet);
        dp = this.dossierRepository.save(dp);
        assertEquals(dp.statut(), StatutDossier.DEPOSE);
        File file = new File("src/test/fixtures/dummy.pdf");
        Optional<PieceJointe> pieceJointe = this.ajouterPieceJointe.execute(dp.identity(), "1",
                new FileInputStream(file), file.getName(), "application/pdf", file.length());
        assertTrue(pieceJointe.isPresent());
        assertEquals(pieceJointe.get().code().type(), TypesDossier.DP);
    }

    @Test
    @WithDeposantBetaDetails
    public void executePCMI1Test() throws IOException, AjouterPieceJointeException, AuthRequiredException,
            UserForbiddenException, UserInfoServiceException, CommuneNotFoundException {
                Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01",
                new ParcelleCadastrale("0", "1", "2"), true);
        Dossier pcmi = this.dossierFactory.creer(this.deposantBeta, TypesDossier.PCMI, projet);
        pcmi = this.dossierRepository.save(pcmi);
        Optional<Dossier> optionalDossier = this.dossierRepository.findById(pcmi.identity().toString());
        assertTrue(optionalDossier.isPresent());
        pcmi = optionalDossier.get();
        assertEquals(pcmi.statut(), StatutDossier.DEPOSE);
        File file = new File("src/test/fixtures/dummy.pdf");
        Optional<PieceJointe> pieceJointe = this.ajouterPieceJointe.execute(pcmi.identity(), "1",
                new FileInputStream(file), file.getName(), "application/pdf", file.length());
        assertTrue(pieceJointe.isPresent());
        assertEquals(pieceJointe.get().code().type(), TypesDossier.PCMI);
    }
}