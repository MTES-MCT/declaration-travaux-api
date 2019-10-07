package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import java.util.Optional;
import java.util.regex.PatternSyntaxException;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaAdresse;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaNature;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaProjet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JpaProjetFactory {

    @Autowired
    private ProjetFactory projetFactory;

    public JpaProjet toJpa(JpaDossier jpaDossier, Projet projet) {
        if (projet.nature() == null)
            throw new NullPointerException("La nature du projet ne peut pas être nulle.");
        JpaNature jpaNature = new JpaNature(projet.nature().nouvelleConstruction());
        if (projet.localisation() == null)
            throw new NullPointerException("La localisation du projet ne peut pas être nulle.");
        JpaAdresse jpaAdresse = new JpaAdresse(projet.localisation().adresse().numero(),
                projet.localisation().adresse().voie(), projet.localisation().adresse().lieuDit(),
                projet.localisation().adresse().commune().codePostal(), projet.localisation().adresse().bp(),
                projet.localisation().adresse().cedex());
        String parcelles = projet.localisation().parcellesCadastrales().stream().map(ParcelleCadastrale::toFlatString).reduce(",", String::join);
        JpaProjet jpaProjet = new JpaProjet(jpaDossier, jpaNature, jpaAdresse, parcelles);
        return jpaProjet;
    }

    public Projet fromJpa(JpaProjet jpaProjet) throws CommuneNotFoundException, PatternSyntaxException {
        if (jpaProjet.getAdresse() == null)
            throw new NullPointerException("L'adresse du projet ne peut pas être nulle.");
        if (jpaProjet.getNature() == null)
            throw new NullPointerException("La nature du projet ne peut pas être nulle.");
        String[] jpaParcelles = jpaProjet.getParcelles().split(",");
        if (jpaParcelles.length < 1)
            throw new NullPointerException("Le projet doit contenir au moins une parcelle cadastrale.");
        Optional<ParcelleCadastrale> parcelle = ParcelleCadastrale.parse(jpaParcelles[0]);
        if (parcelle.isEmpty())
            throw new NullPointerException("Le projet doit contenir au moins une parcelle cadastrale.");
        Projet projet = this.projetFactory.creer(jpaProjet.getAdresse().getNumero(), jpaProjet.getAdresse().getVoie(),
                jpaProjet.getAdresse().getLieuDit(), jpaProjet.getAdresse().getCodePostal(),
                jpaProjet.getAdresse().getBp(), jpaProjet.getAdresse().getCedex(), parcelle.get(),
                jpaProjet.getNature().isConstructionNouvelle());
        if (jpaParcelles.length > 1) {
            for (int i = 1; i < jpaParcelles.length; i++) {
                parcelle = ParcelleCadastrale.parse(jpaParcelles[i]);
                if (parcelle.isPresent())
                    projet.localisation().ajouterParcelle(parcelle.get());
            }
        }
        return projet;
    }
}