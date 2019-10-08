package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.PatternSyntaxException;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.CodePieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.domain.services.DateService;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaAdresse;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaCodePieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDeposant;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaNature;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointeId;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaProjet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class JpaDossierFactoryTests {
    @Autowired
    private DateService dateService;
    @Autowired
    private JpaDossierFactory jpaDossierFactory;
    @Autowired
    private ProjetFactory projetFactory;

    @Test
    public void toJpaTest() throws CommuneNotFoundException {
        Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01",
                new ParcelleCadastrale("0", "1", "2"), true, true);
        Dossier dossier = new Dossier(new DossierId("0"), new Personne("toto", "toto@fai.fr"), StatutDossier.DEPOSE,
                this.dateService.now(), new TypeDossier(TypesDossier.DP, "0"),
                projet);
        dossier.ajouterCerfa(new FichierId("cerfa"));
        dossier.ajouter("1", new FichierId("dp1"));
        JpaDossier jpaDossier = this.jpaDossierFactory.toJpa(dossier);
        assertEquals(jpaDossier.getDeposant(), new JpaDeposant("toto", "toto@fai.fr"));
        assertEquals(jpaDossier.getDateDepot(), this.dateService.now());
        assertEquals(jpaDossier.getDossierId(), "0");
        assertEquals(jpaDossier.getStatut(), StatutDossier.DEPOSE);
        assertEquals(jpaDossier.getType(), TypesDossier.DP);
        assertFalse(jpaDossier.getPiecesJointes().isEmpty());
        assertEquals(jpaDossier.getPiecesJointes().size(), 2);
        assertTrue(jpaDossier.getPiecesJointes().contains(new JpaPieceJointe(
                new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(TypesDossier.DP.toString(), "0"), "cerfa"))));
        assertTrue(jpaDossier.getPiecesJointes().contains(new JpaPieceJointe(
                new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(TypesDossier.DP.toString(), "1"), "dp1"))));
    }

    @Test
    public void fromJpaTest() throws PatternSyntaxException, CommuneNotFoundException {
        JpaDossier jpaDossier = new JpaDossier("0", StatutDossier.DEPOSE, this.dateService.now(), new JpaDeposant("toto", "toto@fai.fr"), TypesDossier.DP);
        JpaProjet jpaProjet = new JpaProjet(jpaDossier, new JpaNature(true), new JpaAdresse("1", "rue des Fleurs", "ZI les roses", "44100", "BP 1", "Cedex 1"), "1-2-3,4-5-6", true);
        JpaPieceJointe cerfa = new JpaPieceJointe(new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(TypesDossier.DP.toString(), "0"), "cerfa"));
        jpaDossier.addPieceJointe(cerfa);
        JpaPieceJointe dp1 = new JpaPieceJointe(new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(TypesDossier.DP.toString(), "1"), "dp1"));
        jpaDossier.addPieceJointe(dp1);
        Dossier dossier = this.jpaDossierFactory.fromJpa(jpaDossier, jpaProjet);
        assertEquals(new Personne("toto", "toto@fai.fr"), dossier.deposant());
        assertEquals(new DossierId("0"), dossier.identity());
        assertEquals(StatutDossier.DEPOSE, dossier.statut());
        assertEquals(new TypeDossier(TypesDossier.DP, "13703"), dossier.type());
        assertNotNull(dossier.cerfa());
        assertEquals(new PieceJointe(dossier, new CodePieceJointe(TypesDossier.DP, "0"), new FichierId("cerfa")), dossier.cerfa());
        assertFalse(dossier.pieceJointes().isEmpty());
        assertEquals(1, dossier.pieceJointes().size());
        assertTrue(dossier.pieceJointes().contains(new PieceJointe(dossier, new CodePieceJointe(TypesDossier.DP, "1"), new FichierId("dp1"))));
    }
    
}