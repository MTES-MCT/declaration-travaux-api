package com.github.mtesmct.rieau.rieauapi.infra.persistence.inmemory;

import java.util.Map;
import java.util.Optional;

import com.github.mtesmct.rieau.rieauapi.domain.entities.Identite;
import com.github.mtesmct.rieau.rieauapi.domain.repositories.IdentiteRepository;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class InMemoryIdentiteRepository implements IdentiteRepository {

    private Map<String, Identite> identites;

    @Override
    public Optional<Identite> findById(String id) {
        return Optional.ofNullable(this.identites.get(id));
    }

    public InMemoryIdentiteRepository(Map<String, Identite> identites) {
        this.identites = identites;
    }

    @Override
    public Identite save(Identite identite) {
        this.identites.put(identite.getId(), identite);
        return identite;
    }
    
}