package com.github.mtesmct.rieau.api.depositaire.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot;

public interface DepotRepository {
    public List<Depot> findAll();
    public Optional<Depot> findById(String id);
    public Depot save(Depot depot);
}