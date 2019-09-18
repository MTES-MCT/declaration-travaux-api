package com.github.mtesmct.rieau.api.application.dossiers;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceNonAJoindreException;

public interface AjouterPieceJointeService {
    public Optional<PieceJointe> execute(Dossier dossier, String numero, Fichier fichier) throws PieceNonAJoindreException;
}
