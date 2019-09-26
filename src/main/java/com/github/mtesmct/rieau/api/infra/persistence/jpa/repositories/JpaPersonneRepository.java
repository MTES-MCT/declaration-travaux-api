package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.repositories.PersonneRepository;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPersonne;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaPersonneFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JpaPersonneRepository implements PersonneRepository {

    @Autowired
    private JpaSpringPersonneRepository jpaSpringPersonneRepository;
    @Autowired
    private JpaPersonneFactory jpaPersonneFactory;

    @Override
    public Optional<Personne> findByPersonneId(String personneId) {
        Optional<JpaPersonne> jpaPersonne = this.jpaSpringPersonneRepository
                .findBySimpleNaturalId(personneId);
        Optional<Personne> personne = Optional.empty();
        if (jpaPersonne.isPresent())
            personne = Optional.ofNullable(this.jpaPersonneFactory.fromJpa(jpaPersonne.get()));
        return personne;
    }

}