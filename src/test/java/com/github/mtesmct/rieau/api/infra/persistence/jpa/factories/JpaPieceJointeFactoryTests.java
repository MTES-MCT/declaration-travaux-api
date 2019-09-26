package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
public class JpaPieceJointeFactoryTests {

    @Autowired
    private JpaPieceJointeFactory jpaPieceJointeFactory;
    @Autowired
    private DateService dateService;

    @Test
    public void toJpaTest() {
        JpaDossier jpaDossier = new JpaDossier("0", StatutDossier.DEPOSE, this.dateService.now(), new JpaDeposant("toto", "toto@fai.fr"), TypesDossier.DP);
        Dossier dossier = new Dossier(new DossierId("0"), new Personne("toto", "toto@fai.fr"), StatutDossier.DEPOSE, this.dateService.now(), new TypeDossier(TypesDossier.DP, "0", 1));
        PieceJointe pieceJointe = new PieceJointe(dossier, new CodePieceJointe(TypesDossier.DP, "0"), new FichierId("0"));
        JpaPieceJointe jpaPieceJointe = this.jpaPieceJointeFactory.toJpa(jpaDossier, pieceJointe);
        assertEquals(jpaPieceJointe.getId().getDossier(), jpaDossier);
        assertEquals(jpaPieceJointe.getId().getCode(), new JpaCodePieceJointe(TypesDossier.DP.toString(), "0"));
        assertEquals(jpaPieceJointe.getId().getFichierId(), "0");
    }

    @Test
    public void fromJpaTest() {
        JpaDossier jpaDossier = new JpaDossier("0", StatutDossier.DEPOSE, this.dateService.now(), new JpaDeposant("toto", "toto@fai.fr"), TypesDossier.DP);
        Dossier dossier = new Dossier(new DossierId("0"), new Personne("toto", "toto@fai.fr"), StatutDossier.DEPOSE, this.dateService.now(), new TypeDossier(TypesDossier.DP, "0", 1));
        JpaPieceJointe jpaPieceJointe = new JpaPieceJointe(new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(TypesDossier.DP.toString(), "0"), "0"));
        PieceJointe pieceJointe = this.jpaPieceJointeFactory.fromJpa(dossier, jpaPieceJointe);
        assertEquals(pieceJointe.dossier(), dossier);
        assertEquals(pieceJointe.code(), new CodePieceJointe(TypesDossier.DP, "0"));
        assertEquals(pieceJointe.fichierId(), new FichierId("0"));
    }
    
}