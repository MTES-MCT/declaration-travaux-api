package com.github.mtesmct.rieau.api.domain.services;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Commune;

import java.util.Optional;

public interface CommuneService {
    public Optional<Commune> findByCodeCodePostal(String codePostal);
}