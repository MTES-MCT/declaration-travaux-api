package com.github.mtesmct.rieau.api.domain.repositories;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.Identite;

public interface IdentiteRepository {
    public Optional<Identite> findById(String id);
    public Identite save(Identite identite);
}