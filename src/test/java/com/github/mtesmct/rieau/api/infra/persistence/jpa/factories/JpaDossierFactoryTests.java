package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.CodePieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.services.DateService;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaCodePieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDeposant;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointeId;

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

    @Test
    public void toJpaTest(){
        Dossier dossier = new Dossier(new DossierId("0"), new Personne("toto", "toto@fai.fr"), StatutDossier.DEPOSE, this.dateService.now(), new TypeDossier(TypesDossier.DP, "0", Arrays.asList(new String[]{"1"})));
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
        assertTrue(jpaDossier.getPiecesJointes().contains(new JpaPieceJointe(new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(TypesDossier.DP.toString(), "0"), "cerfa"))));
        assertTrue(jpaDossier.getPiecesJointes().contains(new JpaPieceJointe(new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(TypesDossier.DP.toString(), "1"), "dp1"))));
    }

    @Test
    public void fromJpaTest(){
        JpaDossier jpaDossier = new JpaDossier("0", StatutDossier.DEPOSE, this.dateService.now(), new JpaDeposant("toto", "toto@fai.fr"), TypesDossier.DP);
        JpaPieceJointe cerfa = new JpaPieceJointe(new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(TypesDossier.DP.toString(), "0"), "cerfa"));
        jpaDossier.addPieceJointe(cerfa);
        JpaPieceJointe dp1 = new JpaPieceJointe(new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(TypesDossier.DP.toString(), "1"), "dp1"));
        jpaDossier.addPieceJointe(dp1);
        Dossier dossier = this.jpaDossierFactory.fromJpa(jpaDossier);
        assertEquals(new Personne("toto", "toto@fai.fr"), dossier.deposant());
        assertEquals(new DossierId("0"), dossier.identity());
        assertEquals(StatutDossier.DEPOSE, dossier.statut());
        assertEquals(new TypeDossier(TypesDossier.DP, "13703", Arrays.asList(new String[]{"1"})), dossier.type());
        assertNotNull(dossier.cerfa());
        assertEquals(new PieceJointe(dossier, new CodePieceJointe(TypesDossier.DP, "0"), new FichierId("cerfa")), dossier.cerfa());
        assertFalse(dossier.pieceJointes().isEmpty());
        assertEquals(1, dossier.pieceJointes().size());
        assertTrue(dossier.pieceJointes().contains(new PieceJointe(dossier, new CodePieceJointe(TypesDossier.DP, "1"), new FichierId("dp1"))));
    }
    
}