package com.github.mtesmct.rieau.api.domain.services;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;

public interface CerfaService {
    public Dossier fromCerfa(String depositaire, PieceJointe pieceJointe) throws CerfaServiceException;
}