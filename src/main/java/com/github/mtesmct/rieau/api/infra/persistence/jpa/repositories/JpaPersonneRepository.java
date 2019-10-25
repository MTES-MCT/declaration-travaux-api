package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.repositories.PersonneRepository;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPersonne;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaPersonneFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class JpaPersonneRepository implements PersonneRepository {

    @Autowired
    private JpaSpringPersonneRepository jpaSpringPersonneRepository;
    @Autowired
    private JpaPersonneFactory jpaPersonneFactory;

    @Override
    public Optional<Personne> findByPersonneId(String personneId) {
        Optional<JpaPersonne> jpaPersonne = this.jpaSpringPersonneRepository.findBySimpleNaturalId(personneId);
        Optional<Personne> personne = Optional.empty();
        if (jpaPersonne.isPresent())
            try {
                personne = Optional.ofNullable(this.jpaPersonneFactory.fromJpa(jpaPersonne.get()));
            } catch (CommuneNotFoundException e) {
                log.debug("{}", e.getMessage());
            }
        return personne;
    }

}