package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.regex.PatternSyntaxException;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.personnes.User;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaAdresse;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaNature;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaProjet;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaUser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class JpaProjetFactoryTests {

    @Autowired
    private JpaProjetFactory jpaProjetFactory;
    @Autowired
    private ProjetFactory projetFactory;
    @Autowired
    @Qualifier("deposantBeta")
    private User deposantBeta;

    @Test
    public void toJpaTest() throws CommuneNotFoundException {
        JpaUser deposant = new JpaUser(this.deposantBeta.identity().toString(), this.deposantBeta.identite().nom(), this.deposantBeta.identite().prenom(), String.join(",", this.deposantBeta.profils()));
        JpaDossier jpaDossier = new JpaDossier("0", deposant, EnumTypes.DPMI);
        Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01",
                new ParcelleCadastrale("0", "1", "2"), true, true);
        JpaProjet jpaProjet = this.jpaProjetFactory.toJpa(jpaDossier, projet);
        assertEquals(projet.localisation().adresse().numero(), jpaProjet.getAdresse().getNumero());
        assertEquals(projet.localisation().adresse().voie(), jpaProjet.getAdresse().getVoie());
        assertEquals(projet.localisation().adresse().bp(), jpaProjet.getAdresse().getBp());
        assertEquals(projet.localisation().adresse().cedex(), jpaProjet.getAdresse().getCedex());
        assertEquals(projet.localisation().parcellesCadastrales().get(0).toFlatString(), jpaProjet.getParcelles());
        assertEquals(projet.nature().nouvelleConstruction(), jpaProjet.getNature().isConstructionNouvelle());
        assertEquals(projet.localisation().lotissement(), jpaProjet.isLotissement());
    }

    @Test
    public void fromJpaTest() throws PatternSyntaxException, CommuneNotFoundException {
        JpaUser deposant = new JpaUser(this.deposantBeta.identity().toString(), this.deposantBeta.identite().nom(), this.deposantBeta.identite().prenom(), String.join(",", this.deposantBeta.profils()));
        JpaDossier jpaDossier = new JpaDossier("0", deposant, EnumTypes.DPMI);
        JpaProjet jpaProjet = new JpaProjet(jpaDossier, new JpaNature(true), new JpaAdresse("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01"), "0-1-2,1-2-3", true);
        Projet projet = this.jpaProjetFactory.fromJpa(jpaProjet);
        assertEquals(jpaProjet.getAdresse().getNumero(), projet.localisation().adresse().numero());
        assertEquals(jpaProjet.getAdresse().getVoie(), projet.localisation().adresse().voie());
        assertEquals(jpaProjet.getAdresse().getBp(), projet.localisation().adresse().bp());
        assertEquals(jpaProjet.getAdresse().getCedex(), projet.localisation().adresse().cedex());
        assertEquals("0-1-2", projet.localisation().parcellesCadastrales().get(0).toFlatString());
        assertEquals("1-2-3", projet.localisation().parcellesCadastrales().get(1).toFlatString());
        assertEquals(jpaProjet.getNature().isConstructionNouvelle(), projet.nature().nouvelleConstruction());
        assertEquals(jpaProjet.isLotissement(), projet.localisation().lotissement());
    }
    
}