package com.github.mtesmct.rieau.api.infra.persistence.inmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.Demande;
import com.github.mtesmct.rieau.api.domain.repositories.DemandeRepository;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class InMemoryDemandeRepository implements DemandeRepository {

    private Map<String, Demande> inMemoryDb;

    public InMemoryDemandeRepository() {
        this.inMemoryDb = new HashMap<String, Demande>();
    }

    public InMemoryDemandeRepository(Map<String, Demande> inMemoryDb) {
        this.inMemoryDb = inMemoryDb;
    }

    @Override
    public List<Demande> findAll() {
        return new ArrayList<>(inMemoryDb.values());
    }

    @Override
    public Optional<Demande> findById(String id) {
        return Optional.ofNullable(inMemoryDb.get(id));
    }

    @Override
    public Demande save(Demande demande) {
        inMemoryDb.put(demande.getId(), demande);
        return demande;
    }
    
}