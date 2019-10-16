package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.regex.PatternSyntaxException;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.AjouterPieceJointeException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.CodePieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumStatuts;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceNonAJoindreException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatut;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.TypeStatutDossierRepository;
import com.github.mtesmct.rieau.api.domain.repositories.TypeDossierRepository;
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
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaStatut;

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
        @Autowired
        private TypeDossierRepository typeDossierRepository;
        @Autowired
        private TypeStatutDossierRepository statutDossierRepository;

        @Test
        public void toJpaTest() throws CommuneNotFoundException, StatutForbiddenException, PieceNonAJoindreException,
                        AjouterPieceJointeException {
        Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01",
                new ParcelleCadastrale("0", "1", "2"), true, true);
                Optional<TypeDossier> type = this.typeDossierRepository.findByType(EnumTypes.DPMI);
                assertTrue(type.isPresent());
        Dossier dossier = new Dossier(new DossierId("0"), new Personne("toto", "toto@fai.fr"), type.get(),
                projet, new FichierId("cerfa"));
        dossier.ajouterPieceJointe("1", new FichierId("dp1"));
        Optional<TypeStatut> typeStatut = statutDossierRepository.findByStatut(EnumStatuts.DEPOSE);
        assertTrue(typeStatut.isPresent());
        dossier.ajouterStatut(this.dateService.now(), typeStatut.get());
        JpaDossier jpaDossier = this.jpaDossierFactory.toJpa(dossier);
        assertEquals(new JpaDeposant("toto", "toto@fai.fr"), jpaDossier.getDeposant());
        assertEquals("0", jpaDossier.getDossierId());
        assertFalse(jpaDossier.getStatuts().isEmpty());
        assertEquals(1, jpaDossier.getStatuts().size());
        JpaStatut jpaStatut = jpaDossier.getStatuts().iterator().next();
        assertEquals(EnumStatuts.DEPOSE, jpaStatut.getStatut());
        assertEquals(EnumTypes.DPMI, jpaDossier.getType());
        assertFalse(jpaDossier.getPiecesJointes().isEmpty());
        assertEquals(jpaDossier.getPiecesJointes().size(), 2);
        assertTrue(jpaDossier.getPiecesJointes().contains(new JpaPieceJointe(
                new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(EnumTypes.DPMI.toString(), "0"), "cerfa"))));
        assertTrue(jpaDossier.getPiecesJointes().contains(new JpaPieceJointe(
                new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(EnumTypes.DPMI.toString(), "1"), "dp1"))));
    }

    @Test
    public void fromJpaTest() throws PatternSyntaxException, CommuneNotFoundException {
        JpaDossier jpaDossier = new JpaDossier("0", new JpaDeposant("toto", "toto@fai.fr"), EnumTypes.DPMI);
        JpaProjet jpaProjet = new JpaProjet(jpaDossier, new JpaNature(true), new JpaAdresse("1", "rue des Fleurs", "ZI les roses", "44100", "BP 1", "Cedex 1"), "1-2-3,4-5-6", true);
        JpaPieceJointe cerfa = new JpaPieceJointe(new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(EnumTypes.DPMI.toString(), "0"), "cerfa"));
        jpaDossier.addPieceJointe(cerfa);
        JpaPieceJointe dp1 = new JpaPieceJointe(new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(EnumTypes.DPMI.toString(), "1"), "dp1"));
        jpaDossier.addPieceJointe(dp1);
        jpaDossier.addStatut(new JpaStatut(jpaDossier, EnumStatuts.DEPOSE, this.dateService.now()));
        Dossier dossier = this.jpaDossierFactory.fromJpa(jpaDossier, jpaProjet);
        assertEquals(new Personne("toto", "toto@fai.fr"), dossier.deposant());
        assertEquals(new DossierId("0"), dossier.identity());
        assertTrue(dossier.statutActuel().isPresent());
        assertEquals(EnumStatuts.DEPOSE, dossier.statutActuel().get().type().statut());
        Optional<TypeDossier> type = this.typeDossierRepository.findByType(EnumTypes.DPMI);
        assertTrue(type.isPresent());
        assertEquals(type.get(), dossier.type());
        assertNotNull(dossier.cerfa());
        assertEquals(new PieceJointe(dossier, new CodePieceJointe(EnumTypes.DPMI, "0"), new FichierId("cerfa")), dossier.cerfa());
        assertFalse(dossier.pieceJointes().isEmpty());
        assertEquals(1, dossier.pieceJointes().size());
        assertTrue(dossier.pieceJointes().contains(new PieceJointe(dossier, new CodePieceJointe(EnumTypes.DPMI, "1"), new FichierId("dp1"))));
    }
    
}