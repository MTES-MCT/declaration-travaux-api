package com.github.mtesmct.rieau.api.domain.repositories;

import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;

import java.util.Optional;

public interface PersonneRepository {
    public Optional<Personne> findByPersonneId(String personneId);
}