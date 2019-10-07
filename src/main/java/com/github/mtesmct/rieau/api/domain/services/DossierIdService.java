package com.github.mtesmct.rieau.api.domain.services;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;

public interface DossierIdService {
    public DossierId creer(String typeDossier, String departement, String commune, String annee);
}