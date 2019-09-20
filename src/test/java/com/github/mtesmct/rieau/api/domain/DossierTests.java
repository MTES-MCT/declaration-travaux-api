package com.github.mtesmct.rieau.api.domain;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.AjouterPieceJointeException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.CodePieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.NumeroPieceJointeException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceNonAJoindreException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.services.FichierIdService;
import com.github.mtesmct.rieau.api.domain.services.FichierService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DossierTests {

    @Autowired
    private DossierFactory dossierFactory;
    @Autowired
    private FichierService fichierService;
    @Autowired
    private FichierIdService fichierIdService;
    @Autowired
    @Qualifier("deposantBeta")
    private Personne deposantBeta;

    private Dossier dossier;
    private Fichier cerfa;
    private Fichier dp1;
    private FichierId dp1Id;
    private FichierId cerfaId;

    @BeforeEach
    public void setUp() throws IOException {
        FileInputStream fis = new FileInputStream(new File("src/test/fixtures/cerfa_13703_DPMI.pdf"));
        this.cerfa = new Fichier("cerfa_13703_DPMI.pdf", "application/pdf", fis, fis.available());
        this.cerfaId = this.fichierIdService.creer();
        this.fichierService.save(this.cerfaId, this.cerfa);
        this.dossier = this.dossierFactory.creer(this.deposantBeta, TypesDossier.DP);
        assertNotNull(this.dossier);
        assertNotNull(this.dossier.piecesAJoindre());
        assertEquals(this.dossier.statut(), StatutDossier.DEPOSE);
        assertEquals(this.dossier.piecesAJoindre().codesPiecesAJoindre().size(), 1);
        assertTrue(this.dossier.piecesAJoindre().codesPiecesAJoindre().contains(new CodePieceJointe(TypesDossier.DP, "1")));
        this.dossier.ajouterCerfa(this.cerfaId);
        fis.close();
        fis = new FileInputStream(new File("src/test/fixtures/dummy.pdf"));
        this.dp1 = new Fichier("dummy.pdf", "application/pdf", fis, fis.available());
        this.dp1Id = this.fichierIdService.creer();
        this.fichierService.save(this.dp1Id, this.dp1);        
        fis.close();      
    }

    @Test
    public void ajouterCerfa() {
        assertNotNull(this.dossier.cerfa());
        assertNotNull(this.dossier.cerfa().code());
        assertTrue(this.dossier.cerfa().code().isCerfa());
        assertNotNull(this.dossier.cerfa().fichierId());
        assertEquals(this.dossier.cerfa().fichierId(), this.cerfaId);
        assertEquals(this.dossier.pieceJointes().size(), 0);
    }

    @Test
    public void ajouterPieceJointeCERFAAuDossier_throwsErreurNumeroInvalide() {
        AjouterPieceJointeException exception = assertThrows(AjouterPieceJointeException.class, () -> this.dossier.ajouter("0", this.dp1Id));
        assertNotNull(exception.getCause());
        assertEquals(exception.getCause().getClass(), NumeroPieceJointeException.class);
    }

    @Test
    public void ajouterPieceJointeCERFAAuDossier_throwsErreurPieceNonAJoindre() {
        AjouterPieceJointeException exception = assertThrows(AjouterPieceJointeException.class, () -> this.dossier.ajouter("2", this.dp1Id));
        assertNotNull(exception.getCause());
        assertEquals(exception.getCause().getClass(), PieceNonAJoindreException.class);
    }

    @Test
    public void ajouterPieceJointeObligatoireAuDossier() {
        Optional<PieceJointe> pjDP1 = this.dossier.ajouter("1", this.dp1Id);
        assertTrue(pjDP1.isPresent());
        assertTrue(pjDP1.get().isAJoindre());
        assertEquals(this.dossier.pieceJointes().size(), 1);
        assertTrue(this.dossier.pieceJointes().contains(pjDP1.get()));
    }

}