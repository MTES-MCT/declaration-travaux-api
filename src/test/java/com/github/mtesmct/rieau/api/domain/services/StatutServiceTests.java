package com.github.mtesmct.rieau.api.domain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Adresse;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Commune;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumStatuts;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Localisation;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Nature;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class StatutServiceTests {
    @Autowired
    private StatutService statutService;
    private Dossier dossier;

    @Test
    public void deposer() throws StatutForbiddenException, TypeStatutNotFoundException {
        Projet projet = new Projet(new Localisation(
                new Adresse("numero", "voie", "lieuDit", new Commune("codePostal", "nom", "department"), "bp", "cedex"),
                new ParcelleCadastrale("prefixe", "section", "numero"), true), new Nature(true));
        Personne deposant = new Personne("test", "test@test.fr");
        this.dossier = new Dossier(new DossierId("0"), deposant,
                new TypeDossier(EnumTypes.DPMI, "0", new ArrayList<String>()), projet, new FichierId("cerfa"));
        this.statutService.deposer(this.dossier);
        assertTrue(this.dossier.statutActuel().isPresent());
        assertEquals(EnumStatuts.DEPOSE, this.dossier.statutActuel().get().type().identity());
        assertFalse(this.dossier.historiqueStatuts().isEmpty());
        assertEquals(1, this.dossier.historiqueStatuts().size());
    }

    @Test
    public void qualifier() throws StatutForbiddenException, TypeStatutNotFoundException {
        this.deposer();
        this.statutService.qualifier(this.dossier);
        assertTrue(this.dossier.statutActuel().isPresent());
        assertEquals(EnumStatuts.QUALIFIE, this.dossier.statutActuel().get().type().identity());
        assertFalse(this.dossier.historiqueStatuts().isEmpty());
        assertEquals(2, this.dossier.historiqueStatuts().size());
    }

    @Test
    public void requalifierInterdit() throws StatutForbiddenException, TypeStatutNotFoundException {
        this.qualifier();
        StatutForbiddenException exception = assertThrows(StatutForbiddenException.class, () -> this.statutService.qualifier(this.dossier));
        assertEquals(StatutForbiddenException.messageDejaPresent(EnumStatuts.QUALIFIE), exception.getMessage());
    }

    @Test
    public void declarerCompletInterdit() throws StatutForbiddenException, TypeStatutNotFoundException {
        this.qualifier();
        StatutForbiddenException exception = assertThrows(StatutForbiddenException.class, () -> this.statutService.declarerComplet(this.dossier));
        assertEquals(StatutForbiddenException.messageNonConsecutif(EnumStatuts.COMPLET, EnumStatuts.QUALIFIE), exception.getMessage());
    }

    @Test
    public void instruire() throws StatutForbiddenException, TypeStatutNotFoundException {
        this.qualifier();
        this.statutService.instruire(this.dossier);
        assertTrue(this.dossier.statutActuel().isPresent());
        assertEquals(EnumStatuts.INSTRUCTION, this.dossier.statutActuel().get().type().identity());
        assertFalse(this.dossier.historiqueStatuts().isEmpty());
        assertEquals(3, this.dossier.historiqueStatuts().size());
    }

    @Test
    public void declarerIncomplet() throws StatutForbiddenException, TypeStatutNotFoundException {
        this.instruire();
        Personne auteur = new Personne("test", "test@test.fr");
        this.statutService.declarerIncomplet(this.dossier, auteur, "Incomplet!");
        assertTrue(this.dossier.statutActuel().isPresent());
        assertEquals(EnumStatuts.INCOMPLET, this.dossier.statutActuel().get().type().identity());
        assertFalse(this.dossier.historiqueStatuts().isEmpty());
        assertEquals(4, this.dossier.historiqueStatuts().size());
        assertFalse(this.dossier.messages().isEmpty());
        assertEquals(1, this.dossier.messages().size());
        assertEquals(auteur, this.dossier.messages().get(0).auteur());
        assertEquals("Incomplet!", this.dossier.messages().get(0).contenu());
    }

    @Test
    public void declarerComplet() throws StatutForbiddenException, TypeStatutNotFoundException {
        this.declarerIncomplet();
        this.statutService.declarerComplet(this.dossier);
        assertTrue(this.dossier.statutActuel().isPresent());
        assertEquals(EnumStatuts.COMPLET, this.dossier.statutActuel().get().type().identity());
        assertFalse(this.dossier.historiqueStatuts().isEmpty());
        assertEquals(5, this.dossier.historiqueStatuts().size());
        assertFalse(this.dossier.messages().isEmpty());
        assertEquals(1, this.dossier.messages().size());
    }

    @Test
    public void lancerConsultations() throws StatutForbiddenException, TypeStatutNotFoundException {
        this.declarerComplet();
        this.statutService.lancerConsultations(this.dossier);
        assertTrue(this.dossier.statutActuel().isPresent());
        assertEquals(EnumStatuts.CONSULTATIONS, this.dossier.statutActuel().get().type().identity());
        assertFalse(this.dossier.historiqueStatuts().isEmpty());
        assertEquals(6, this.dossier.historiqueStatuts().size());
        assertFalse(this.dossier.messages().isEmpty());
        assertEquals(1, this.dossier.messages().size());
    }

    @Test
    public void prononcerDecision() throws StatutForbiddenException, TypeStatutNotFoundException {
        this.lancerConsultations();
        this.statutService.prononcerDecision(this.dossier);
        assertTrue(this.dossier.statutActuel().isPresent());
        assertEquals(EnumStatuts.DECISION, this.dossier.statutActuel().get().type().identity());
        assertFalse(this.dossier.historiqueStatuts().isEmpty());
        assertEquals(7, this.dossier.historiqueStatuts().size());
        assertFalse(this.dossier.messages().isEmpty());
        assertEquals(1, this.dossier.messages().size());
    }

}