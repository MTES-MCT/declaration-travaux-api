package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.CodePieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.TypeDossierRepository;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
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
        private ProjetFactory projetFactory;
        @Autowired
        private TypeDossierRepository typeDossierRepository;
        @Autowired
        private FichierService fichierService;
        @Autowired
        private FichierFactory fichierFactory;

        @Test
        public void toJpaTest() throws CommuneNotFoundException, FileNotFoundException {
                JpaDossier jpaDossier = new JpaDossier("0", new JpaDeposant("toto", "toto@fai.fr"), EnumTypes.DPMI);
                Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44",
                                "Cedex 01", new ParcelleCadastrale("0", "1", "2"), true, true);
                Optional<TypeDossier> type = this.typeDossierRepository.findByType(EnumTypes.DPMI);
                assertTrue(type.isPresent());
                File cerfaFile = new File("src/test/fixtures/dummy.pdf");
                Fichier cerfaFichier = this.fichierFactory.creer(cerfaFile, "application/pdf");
                this.fichierService.save(cerfaFichier);
                Dossier dossier = new Dossier(new DossierId("0"), new Personne("toto", "toto@fai.fr"), type.get(),
                                projet, cerfaFichier.identity());
                PieceJointe pieceJointe = new PieceJointe(dossier, new CodePieceJointe(EnumTypes.DPMI, "0"),
                                new FichierId("0"));
                JpaPieceJointe jpaPieceJointe = this.jpaPieceJointeFactory.toJpa(jpaDossier, pieceJointe);
                assertEquals(jpaPieceJointe.getId().getDossier(), jpaDossier);
                assertEquals(jpaPieceJointe.getId().getCode(), new JpaCodePieceJointe(EnumTypes.DPMI.toString(), "0"));
                assertEquals(jpaPieceJointe.getId().getFichierId(), "0");
        }

        @Test
        public void fromJpaTest() throws CommuneNotFoundException, FileNotFoundException {
                JpaDossier jpaDossier = new JpaDossier("0", new JpaDeposant("toto", "toto@fai.fr"), EnumTypes.DPMI);
                Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44",
                                "Cedex 01", new ParcelleCadastrale("0", "1", "2"), true, true);
                Optional<TypeDossier> type = this.typeDossierRepository.findByType(EnumTypes.DPMI);
                assertTrue(type.isPresent());
                File cerfaFile = new File("src/test/fixtures/dummy.pdf");
                Fichier cerfaFichier = this.fichierFactory.creer(cerfaFile, "application/pdf");
                this.fichierService.save(cerfaFichier);
                Dossier dossier = new Dossier(new DossierId("0"), new Personne("toto", "toto@fai.fr"), type.get(),
                                projet, cerfaFichier.identity());
                JpaPieceJointe jpaPieceJointe = new JpaPieceJointe(new JpaPieceJointeId(jpaDossier,
                                new JpaCodePieceJointe(EnumTypes.DPMI.toString(), "0"), "0"));
                PieceJointe pieceJointe = this.jpaPieceJointeFactory.fromJpa(dossier, jpaPieceJointe);
                assertEquals(pieceJointe.dossier(), dossier);
                assertEquals(pieceJointe.code(), new CodePieceJointe(EnumTypes.DPMI, "0"));
                assertEquals(pieceJointe.fichierId(), new FichierId("0"));
        }

}