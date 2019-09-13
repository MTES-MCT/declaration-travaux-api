package com.github.mtesmct.rieau.api.application.dossiers;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;

public interface ConsulterMonDossierService {
    public Optional<Dossier> execute(String id);
}
