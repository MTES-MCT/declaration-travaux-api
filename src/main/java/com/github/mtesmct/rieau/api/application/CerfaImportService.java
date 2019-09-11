package com.github.mtesmct.rieau.api.application;

import java.io.File;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;

public interface CerfaImportService {
    public PieceJointe lire(String demandeur, File file) throws DossierImportException;
}