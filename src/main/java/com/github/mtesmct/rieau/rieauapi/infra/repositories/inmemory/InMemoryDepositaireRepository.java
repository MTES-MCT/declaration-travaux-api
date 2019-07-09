package com.github.mtesmct.rieau.rieauapi.infra.repositories.inmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.mtesmct.rieau.rieauapi.domain.entities.Demande;
import com.github.mtesmct.rieau.rieauapi.domain.repositories.DepositaireRepository;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class InMemoryDepositaireRepository implements DepositaireRepository {

    private Map<String, Demande> inMemoryDb;

    public InMemoryDepositaireRepository() {
        this.inMemoryDb = new HashMap<String, Demande>();
    }

    public InMemoryDepositaireRepository(Map<String, Demande> inMemoryDb) {
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