package com.github.mtesmct.rieau.api.application.dossiers;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;

public interface CerfaImportService {
    public Optional<String> lireCode(Fichier fichier) throws CerfaImportException;
    public Optional<String> lireFormValue(Fichier fichier, TypesDossier type, String attribut) throws CerfaImportException;
}