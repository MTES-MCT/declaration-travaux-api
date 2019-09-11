package com.github.mtesmct.rieau.api.infra.persistence.jpa.factories;

import com.github.mtesmct.rieau.api.domain.entities.personnes.Naissance;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysiqueId;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPersonnePhysique;

import org.springframework.stereotype.Component;

@Component
public class JpaPersonnePhysiqueFactory {
    public JpaPersonnePhysique toJpa(PersonnePhysique personnePhysique){
        return JpaPersonnePhysique.builder().personnePhysiqueId(personnePhysique.identity().toString()).email(personnePhysique.email()).nom(personnePhysique.nom()).prenom(personnePhysique.prenom()).dateNaissance(personnePhysique.naissance().date()).communeNaissance(personnePhysique.naissance().commune()).build();
    }
    public PersonnePhysique fromJpa(JpaPersonnePhysique jpaPersonnePhysique){
        return new PersonnePhysique(new PersonnePhysiqueId(jpaPersonnePhysique.getPersonnePhysiqueId()), jpaPersonnePhysique.getEmail(), jpaPersonnePhysique.getNom(), jpaPersonnePhysique.getPrenom(), jpaPersonnePhysique.getSexe(), new Naissance(jpaPersonnePhysique.getDateNaissance(), jpaPersonnePhysique.getCommuneNaissance()));
    }
}