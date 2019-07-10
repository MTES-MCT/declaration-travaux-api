package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.Identite;
import com.github.mtesmct.rieau.api.domain.repositories.IdentiteRepository;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaIdentite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JpaIdentiteRepository implements IdentiteRepository {

    @Autowired
    private JpaSpringIdentiteRepository jpaSpringRepository;

    @Override
    public Optional<Identite> findById(String id) {
        Optional<JpaIdentite> jpaEntity = this.jpaSpringRepository.findById(id);
        Optional<Identite> identite = Optional.empty();
        if (jpaEntity.isPresent()){
            identite = Optional.ofNullable(new Identite(jpaEntity.get().getId(), jpaEntity.get().getNom(), jpaEntity.get().getPrenom(), jpaEntity.get().getEmail()));
        }
        return identite;
    }

    @Override
    public Identite save(Identite identite) {
        JpaIdentite jpaIdentite = JpaIdentite.builder().nom(identite.getNom()).prenom(identite.getPrenom()).email(identite.getEmail()).id(identite.getId()).build();
        this.jpaSpringRepository.save(jpaIdentite);
        identite.setId(jpaIdentite.getId());
        return identite;
    }
    
}