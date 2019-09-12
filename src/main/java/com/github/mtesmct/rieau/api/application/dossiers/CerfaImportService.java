package com.github.mtesmct.rieau.api.application.dossiers;

import java.io.File;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;

public interface CerfaImportService {
    public PieceJointe lire(String deposant, File file) throws DossierImportException;
}