package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import com.github.mtesmct.rieau.api.domain.entities.personnes.Naissance;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaNaissance;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPersonne;

import org.springframework.stereotype.Component;

@Component
public class JpaPersonneFactory {
    public JpaPersonne toJpa(Personne personne) {
        JpaNaissance jpaNaissance = null;
        if (personne.naissance() != null)
            jpaNaissance = new JpaNaissance(personne.naissance().date(), personne.naissance().commune());
        JpaPersonne jpaPersonne = new JpaPersonne(personne.identity().toString(), personne.email(), personne.sexe(),
                personne.nom(), personne.prenom(), jpaNaissance);
        return jpaPersonne;
    }

    public Personne fromJpa(JpaPersonne jpaPersonne) {
        Naissance naissance = null;
        if (jpaPersonne.getNaissance() != null)
            naissance = new Naissance(jpaPersonne.getNaissance().getDate(), jpaPersonne.getNaissance().getCommune());
        return new Personne(jpaPersonne.getPersonneId(), jpaPersonne.getEmail(), jpaPersonne.getNom(),
                jpaPersonne.getPrenom(), jpaPersonne.getSexe(), naissance);
    }
}