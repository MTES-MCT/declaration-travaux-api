package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;
import com.github.mtesmct.rieau.api.domain.repositories.PersonnePhysiqueRepository;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPersonnePhysique;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaPersonnePhysiqueFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JpaPersonnePhysiqueRepository implements PersonnePhysiqueRepository {

    @Autowired
    private JpaSpringPersonnePhysiqueRepository jpaSpringPersonnePhysiqueRepository;
    @Autowired
    private JpaPersonnePhysiqueFactory jpaPersonnePhysiqueAdapter;

    @Override
    public Optional<PersonnePhysique> findByPersonnePhysiqueId(String personnePhysiqueId) {
        Optional<JpaPersonnePhysique> jpaPersonnePhysique = this.jpaSpringPersonnePhysiqueRepository
                .findBySimpleNaturalId(personnePhysiqueId);
        Optional<PersonnePhysique> personnePhysique = Optional.empty();
        if (jpaPersonnePhysique.isPresent())
            personnePhysique = Optional.ofNullable(this.jpaPersonnePhysiqueAdapter.fromJpa(jpaPersonnePhysique.get()));
        return personnePhysique;
    }

}