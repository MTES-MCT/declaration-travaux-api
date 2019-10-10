package com.github.mtesmct.rieau.api.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.AjouterPieceJointeException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.NumeroPieceJointeException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceNonAJoindreException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.domain.services.FichierService;

import org.junit.jupiter.api.AfterEach;
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
    private FichierFactory fichierFactory;
    @Autowired
    private ProjetFactory projetFactory;
    @Autowired
    private FichierService fichierService;
    @Autowired
    @Qualifier("deposantBeta")
    private Personne deposantBeta;

    private Dossier dossierDP;
    private Dossier dossierPCMI;
    private Fichier cerfaDP;
    private Fichier cerfaPCMI;
    private Fichier dp1;

    @BeforeEach
    public void setUp() throws IOException, CommuneNotFoundException {
        File file = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
        this.cerfaDP = this.fichierFactory.creer(file, "application/pdf");
        this.fichierService.save(this.cerfaDP);
        Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01",
                new ParcelleCadastrale("0", "1", "2"), true, true);
        this.dossierDP = this.dossierFactory.creer(this.deposantBeta, TypesDossier.DP, projet);
        assertNotNull(this.dossierDP);
        assertNotNull(this.dossierDP.piecesAJoindre());
        assertEquals(StatutDossier.DEPOSE, this.dossierDP.statut());
        assertEquals(2, this.dossierDP.piecesAJoindre().size());
        assertTrue(this.dossierDP.piecesAJoindre().contains("1"));
        this.dossierDP.ajouterCerfa(this.cerfaDP.identity());
        file = new File("src/test/fixtures/dummy.pdf");
        this.dp1 = this.fichierFactory.creer(file, "application/pdf");
        this.fichierService.save(this.dp1);
    }

    @AfterEach
    public void cleanUp() {
        this.fichierService.delete(this.cerfaDP.identity());
        this.fichierService.delete(this.dp1.identity());
    }

    @Test
    public void ajouterCerfaDPMI() {
        assertNotNull(this.dossierDP.cerfa());
        assertNotNull(this.dossierDP.cerfa().code());
        assertTrue(this.dossierDP.cerfa().code().isCerfa());
        assertNotNull(this.dossierDP.cerfa().fichierId());
        assertEquals(this.cerfaDP.identity(), this.dossierDP.cerfa().fichierId());
        assertEquals(0, this.dossierDP.pieceJointes().size());
        assertEquals(2, this.dossierDP.piecesAJoindre().size());
        assertIterableEquals(Arrays.asList(new String[] { "1", "2" }), this.dossierDP.piecesAJoindre());
        assertNotNull(this.dossierDP.projet());
        assertNotNull(this.dossierDP.projet().localisation());
        assertNotNull(this.dossierDP.projet().localisation().adresse());
        assertNotNull(this.dossierDP.projet().localisation().adresse().commune());
        assertEquals("44100", this.dossierDP.projet().localisation().adresse().commune().codePostal());
        assertEquals(1, this.dossierDP.projet().localisation().parcellesCadastrales().size());
        assertIterableEquals(Arrays.asList(new String[] { "0-1-2" }), this.dossierDP.projet().localisation()
                .parcellesCadastrales().stream().map(ParcelleCadastrale::toFlatString).collect(Collectors.toList()));
    }

    @Test
    public void ajouterCerfaPCMI() throws FileNotFoundException, CommuneNotFoundException {
        File file = new File("src/test/fixtures/cerfa_13406_PCMI.pdf");
        this.cerfaPCMI = this.fichierFactory.creer(file, "application/pdf");
        this.fichierService.save(this.cerfaPCMI);
        Projet projet = this.projetFactory.creer("2", "rue des Fleurs", "ZI des Lilas", "44500", "BP 44", "Cedex 01",
                new ParcelleCadastrale("3", "4", "5"), true, true);
        projet.localisation().ajouterParcelle(new ParcelleCadastrale("6", "7", "8"));
        this.dossierPCMI = this.dossierFactory.creer(this.deposantBeta, TypesDossier.PCMI, projet);
        assertNotNull(this.dossierPCMI);
        assertNotNull(this.dossierPCMI.piecesAJoindre());
        assertEquals(StatutDossier.DEPOSE, this.dossierPCMI.statut());
        this.dossierPCMI.ajouterCerfa(this.cerfaPCMI.identity());
        assertNotNull(this.dossierPCMI.cerfa());
        assertNotNull(this.dossierPCMI.cerfa().code());
        assertTrue(this.dossierPCMI.cerfa().code().isCerfa());
        assertNotNull(this.dossierPCMI.cerfa().fichierId());
        assertEquals(this.cerfaPCMI.identity(), this.dossierPCMI.cerfa().fichierId());
        assertEquals(0, this.dossierPCMI.pieceJointes().size());
        assertEquals(10, this.dossierPCMI.piecesAJoindre().size());
        assertIterableEquals(Arrays.asList(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }),
                this.dossierPCMI.piecesAJoindre());
        assertNotNull(this.dossierPCMI.projet());
        assertNotNull(this.dossierPCMI.projet().localisation());
        assertNotNull(this.dossierPCMI.projet().localisation().adresse());
        assertEquals(projet.localisation().adresse().numero(),
                this.dossierPCMI.projet().localisation().adresse().numero());
        assertEquals(projet.localisation().adresse().voie(), this.dossierPCMI.projet().localisation().adresse().voie());
        assertEquals(projet.localisation().adresse().lieuDit(),
                this.dossierPCMI.projet().localisation().adresse().lieuDit());
        assertEquals(projet.localisation().adresse().bp(), this.dossierPCMI.projet().localisation().adresse().bp());
        assertEquals(projet.localisation().adresse().cedex(),
                this.dossierPCMI.projet().localisation().adresse().cedex());
        assertNotNull(this.dossierPCMI.projet().localisation().adresse().commune());
        assertEquals(projet.localisation().adresse().commune().codePostal(),
                this.dossierPCMI.projet().localisation().adresse().commune().codePostal());
        assertEquals(2, this.dossierPCMI.projet().localisation().parcellesCadastrales().size());
        assertIterableEquals(Arrays.asList(new String[] { "3-4-5", "6-7-8" }), this.dossierPCMI.projet().localisation()
                .parcellesCadastrales().stream().map(ParcelleCadastrale::toFlatString).collect(Collectors.toList()));
        this.fichierService.delete(this.cerfaPCMI.identity());
    }

    @Test
    public void ajouterPieceJointeAuDossierDP_throwsErreurNumeroInvalide() {
        AjouterPieceJointeException exception = assertThrows(AjouterPieceJointeException.class,
                () -> this.dossierDP.ajouter("0", this.dp1.identity()));
        assertNotNull(exception.getCause());
        assertEquals(exception.getCause().getClass(), NumeroPieceJointeException.class);
    }

    @Test
    public void ajouterPieceJointeAuDossierDP_throwsErreurPieceNonAJoindre() {
        AjouterPieceJointeException exception = assertThrows(AjouterPieceJointeException.class,
                () -> this.dossierDP.ajouter("3", this.dp1.identity()));
        assertNotNull(exception.getCause());
        assertEquals(exception.getCause().getClass(), PieceNonAJoindreException.class);
    }

    @Test
    public void ajouterPieceJointeObligatoireAuDossierDP() {
        Optional<PieceJointe> pjDP1 = this.dossierDP.ajouter("1", this.dp1.identity());
        assertTrue(pjDP1.isPresent());
        assertTrue(pjDP1.get().isAJoindre());
        assertEquals(this.dossierDP.pieceJointes().size(), 1);
        assertTrue(this.dossierDP.pieceJointes().contains(pjDP1.get()));
    }

    @Test
    public void qualifierDossier() {
        this.dossierDP.qualifier();
        assertEquals(StatutDossier.QUALIFIE, this.dossierDP.statut());
    }

}