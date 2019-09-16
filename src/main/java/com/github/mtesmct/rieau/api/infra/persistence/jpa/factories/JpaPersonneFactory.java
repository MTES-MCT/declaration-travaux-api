package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import com.github.mtesmct.rieau.api.domain.entities.personnes.Naissance;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonneId;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPersonne;

import org.springframework.stereotype.Component;

@Component
public class JpaPersonneFactory {
    public JpaPersonne toJpa(Personne personne){
        return JpaPersonne.builder().personneId(personne.identity().toString()).email(personne.email()).nom(personne.nom()).prenom(personne.prenom()).dateNaissance(personne.naissance().date()).communeNaissance(personne.naissance().commune()).build();
    }
    public Personne fromJpa(JpaPersonne jpaPersonne){
        return new Personne(new PersonneId(jpaPersonne.getPersonneId()), jpaPersonne.getEmail(), jpaPersonne.getNom(), jpaPersonne.getPrenom(), jpaPersonne.getSexe(), new Naissance(jpaPersonne.getDateNaissance(), jpaPersonne.getCommuneNaissance()));
    }
}