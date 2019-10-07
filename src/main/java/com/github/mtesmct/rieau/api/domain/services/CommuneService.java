package com.github.mtesmct.rieau.api.domain.services;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Commune;

public interface CommuneService {
    public Optional<Commune> findByCodeCodePostal(String codePostal);
}