package com.github.mtesmct.rieau.api.depositaire.domain.repositories;

import java.util.Optional;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Identite;

public interface IdentiteRepository {
    public Optional<Identite> findById(String id);
    public Identite save(Identite identite);
}