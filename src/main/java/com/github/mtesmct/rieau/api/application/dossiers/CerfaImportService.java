package com.github.mtesmct.rieau.api.application.dossiers;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;

public interface CerfaImportService {
    public Optional<String> lireCode(Fichier fichier) throws CerfaImportException;
}