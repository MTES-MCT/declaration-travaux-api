package com.github.mtesmct.rieau.api.domain.services;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Commune;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;

public interface DossierIdService {
    public DossierId creer(String typeDossier, Commune commune);
}