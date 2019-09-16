package com.github.mtesmct.rieau.api.domain.repositories;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;

public interface PersonneRepository {
    public Optional<Personne> findByPersonneId(String personneId);
}