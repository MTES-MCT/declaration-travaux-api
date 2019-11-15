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
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceNonAJoindreException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.User;
import com.github.mtesmct.rieau.api.domain.factories.UserFactory;
import com.github.mtesmct.rieau.api.domain.factories.UserParseException;
import com.github.mtesmct.rieau.api.domain.factories.PersonneParseException;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.TypeDossierRepository;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.domain.services.DateService;
import com.github.mtesmct.rieau.api.domain.services.StatutService;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaAdresse;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaCodePieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaMessage;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaNature;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointeId;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaProjet;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaStatut;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaUser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
        private StatutService statutService;
        @Autowired
        @Qualifier("instructeurNonBeta")
        private User instructeur;
        @Autowired
        @Qualifier("deposantBeta")
        private User deposantBeta;
        @Autowired
        private UserFactory userFactory;

        @Test
        public void toJpaTest() throws CommuneNotFoundException, StatutForbiddenException, PieceNonAJoindreException,
                        AjouterPieceJointeException, TypeStatutNotFoundException, UserParseException {
                Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44",
                                "Cedex 01", new ParcelleCadastrale("0", "1", "2"), true, true);
                Optional<TypeDossier> type = this.typeDossierRepository.findByType(EnumTypes.DPMI);
                assertTrue(type.isPresent());
                Dossier dossier = new Dossier(new DossierId("0"), deposantBeta, type.get(),
                                projet, new FichierId("cerfa"));
                dossier.ajouterPieceJointe("1", new FichierId("dp1"));
                this.statutService.deposer(dossier);
                this.statutService.qualifier(dossier);
                this.statutService.declarerIncomplet(dossier, this.instructeur, "Incomplet!");
                JpaDossier jpaDossier = this.jpaDossierFactory.toJpa(dossier);
                assertEquals(this.deposantBeta.identity().toString(), jpaDossier.getDeposant().getId());
                assertEquals(this.deposantBeta.identite().nom(), jpaDossier.getDeposant().getNom());
                assertEquals(this.deposantBeta.identite().prenom(), jpaDossier.getDeposant().getPrenom());
                assertEquals(String.join(",", this.deposantBeta.profils()), jpaDossier.getDeposant().getProfils());
                assertEquals("0", jpaDossier.getDossierId());
                assertFalse(jpaDossier.getStatuts().isEmpty());
                assertEquals(3, jpaDossier.getStatuts().size());
                JpaStatut jpaStatut = jpaDossier.getStatuts().iterator().next();
                assertEquals(EnumStatuts.DEPOSE, jpaStatut.getStatut());
                assertEquals(EnumTypes.DPMI, jpaDossier.getType());
                assertFalse(jpaDossier.getPiecesJointes().isEmpty());
                assertEquals(jpaDossier.getPiecesJointes().size(), 2);
                assertTrue(jpaDossier.getPiecesJointes().contains(new JpaPieceJointe(new JpaPieceJointeId(jpaDossier,
                                new JpaCodePieceJointe(EnumTypes.DPMI.toString(), "0"), "cerfa"))));
                assertTrue(jpaDossier.getPiecesJointes().contains(new JpaPieceJointe(new JpaPieceJointeId(jpaDossier,
                                new JpaCodePieceJointe(EnumTypes.DPMI.toString(), "1"), "dp1"))));
                assertFalse(jpaDossier.getMessages().isEmpty());
                assertEquals(1, jpaDossier.getMessages().size());
                JpaMessage jpaMessage = jpaDossier.getMessages().iterator().next();
                Optional<User> auteur = this.userFactory.parse(jpaMessage.getAuteur());
                assertTrue(auteur.isPresent());
                assertEquals(this.instructeur.identity(), auteur.get().identity());
                assertEquals(this.instructeur.identite().nom(), auteur.get().identite().nom());
                assertEquals(this.instructeur.identite().prenom(), auteur.get().identite().prenom());
                assertEquals(String.join(",", this.instructeur.profils()), String.join(",", auteur.get().profils()));
                assertEquals("Incomplet!", jpaMessage.getContenu());
        }

        @Test
        public void fromJpaTest() throws PatternSyntaxException, CommuneNotFoundException, PersonneParseException {
                JpaUser deposant = new JpaUser(this.deposantBeta.identity().toString(), this.deposantBeta.identite().nom(), this.deposantBeta.identite().prenom(), String.join(",", this.deposantBeta.profils()));
                JpaDossier jpaDossier = new JpaDossier("0", deposant, EnumTypes.DPMI);
                JpaProjet jpaProjet = new JpaProjet(jpaDossier, new JpaNature(true),
                                new JpaAdresse("1", "rue des Fleurs", "ZI les roses", "44100", "BP 1", "Cedex 1"),
                                "1-2-3,4-5-6", true);
                jpaDossier.addProjet(jpaProjet);
                JpaPieceJointe cerfa = new JpaPieceJointe(new JpaPieceJointeId(jpaDossier,
                                new JpaCodePieceJointe(EnumTypes.DPMI.toString(), "0"), "cerfa"));
                jpaDossier.addPieceJointe(cerfa);
                JpaPieceJointe dp1 = new JpaPieceJointe(new JpaPieceJointeId(jpaDossier,
                                new JpaCodePieceJointe(EnumTypes.DPMI.toString(), "1"), "dp1"));
                jpaDossier.addPieceJointe(dp1);
                jpaDossier.addStatut(new JpaStatut(jpaDossier, EnumStatuts.DEPOSE, this.dateService.now()));
                jpaDossier.addMessage(new JpaMessage(jpaDossier, this.instructeur.toString(), this.dateService.now(), "Incomplet!"));
                Dossier dossier = this.jpaDossierFactory.fromJpa(jpaDossier);
                assertEquals(this.deposantBeta.identity().toString(), dossier.deposant().identity().toString());
                assertEquals(this.deposantBeta.identite().nom(), dossier.deposant().identite().nom());
                assertEquals(this.deposantBeta.identite().prenom(), dossier.deposant().identite().prenom());
                assertEquals(String.join(",", this.deposantBeta.profils()), String.join(",", dossier.deposant().profils()));
                assertEquals(new DossierId("0"), dossier.identity());
                assertTrue(dossier.statutActuel().isPresent());
                assertEquals(EnumStatuts.DEPOSE, dossier.statutActuel().get().type().identity());
                Optional<TypeDossier> type = this.typeDossierRepository.findByType(EnumTypes.DPMI);
                assertTrue(type.isPresent());
                assertEquals(type.get(), dossier.type());
                assertNotNull(dossier.cerfa());
                assertEquals(new PieceJointe(dossier, new CodePieceJointe(EnumTypes.DPMI, "0"), new FichierId("cerfa")),
                                dossier.cerfa());
                assertFalse(dossier.pieceJointes().isEmpty());
                assertEquals(1, dossier.pieceJointes().size());
                assertTrue(dossier.pieceJointes().contains(new PieceJointe(dossier,
                                new CodePieceJointe(EnumTypes.DPMI, "1"), new FichierId("dp1"))));
                assertFalse(dossier.messages().isEmpty());
                assertEquals(1, dossier.messages().size());
                User auteur = dossier.messages().get(0).auteur();
                assertEquals(this.instructeur.identity(), auteur.identity());
                assertEquals(this.instructeur.identite().nom(), auteur.identite().nom());
                assertEquals(this.instructeur.identite().prenom(), auteur.identite().prenom());
                assertEquals(String.join(",", this.instructeur.profils()), String.join(",", auteur.profils()));
                assertEquals("Incomplet!", dossier.messages().get(0).contenu());
        }

}