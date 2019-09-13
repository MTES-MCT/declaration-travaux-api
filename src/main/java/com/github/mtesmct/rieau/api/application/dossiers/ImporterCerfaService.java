package com.github.mtesmct.rieau.api.application.dossiers;

import java.io.File;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;

public interface ImporterCerfaService {
    public Optional<Dossier> execute(File file) throws DossierImportException;
}
