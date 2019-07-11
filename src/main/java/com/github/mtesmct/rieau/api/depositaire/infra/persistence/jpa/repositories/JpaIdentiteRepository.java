package com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.repositories;

import java.util.Optional;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Identite;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.IdentiteRepository;
import com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.adapters.IdentitePersistenceAdapter;
import com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.entities.JpaIdentite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JpaIdentiteRepository implements IdentiteRepository {

    @Autowired
    private JpaSpringIdentiteRepository jpaSpringRepository;
    @Autowired
    private IdentitePersistenceAdapter adapter;

    @Override
    public Optional<Identite> findById(String id) {
        Optional<JpaIdentite> jpaEntity = this.jpaSpringRepository.findById(id);
        Optional<Identite> identite = Optional.empty();
        if (jpaEntity.isPresent()){
            identite = Optional.ofNullable(this.adapter.fromJpa(jpaEntity.get()));
        }
        return identite;
    }

    @Override
    public Identite save(Identite identite) {
        JpaIdentite jpaIdentite = this.adapter.toJpa(identite);
        this.jpaSpringRepository.save(jpaIdentite);
        identite.setId(jpaIdentite.getId());
        return identite;
    }
    
}