package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Adresse;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Naissance;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaAdresse;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaNaissance;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPersonne;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JpaPersonneFactory {

    @Autowired
    private JpaAdresseFactory jpaAdresseFactory;

    public JpaPersonne toJpa(Personne personne) {
        JpaNaissance jpaNaissance = null;
        if (personne.naissance() != null)
            jpaNaissance = new JpaNaissance(personne.naissance().date(), personne.naissance().commune());
        JpaAdresse jpaAdresse = null;
        if (personne.adresse() != null)
            jpaAdresse = this.jpaAdresseFactory.toJpa(personne.adresse());
        JpaPersonne jpaPersonne = new JpaPersonne(personne.identity().toString(), personne.email(), personne.sexe(),
                personne.nom(), personne.prenom(), jpaNaissance, jpaAdresse);
        return jpaPersonne;
    }

    public Personne fromJpa(JpaPersonne jpaPersonne) throws CommuneNotFoundException {
        Naissance naissance = null;
        if (jpaPersonne.getNaissance() != null)
            naissance = new Naissance(jpaPersonne.getNaissance().getDate(), jpaPersonne.getNaissance().getCommune());
        Adresse adresse = null;
        if (jpaPersonne.getAdresse() != null)
            adresse = this.jpaAdresseFactory.fromJpa(jpaPersonne.getAdresse());
        return new Personne(jpaPersonne.getPersonneId(), jpaPersonne.getEmail(), jpaPersonne.getNom(),
                jpaPersonne.getPrenom(), jpaPersonne.getSexe(), naissance, adresse);
    }
}