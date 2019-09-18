package com.github.mtesmct.rieau.api.application.dossiers;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;

public interface ImporterCerfaService {
    public Optional<Dossier> execute(Fichier fichier) throws DossierImportException;
}
