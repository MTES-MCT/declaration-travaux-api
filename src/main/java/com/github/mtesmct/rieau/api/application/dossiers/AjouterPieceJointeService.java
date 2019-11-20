package com.github.mtesmct.rieau.api.application.dossiers;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.AjouterPieceJointeException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceNonAJoindreException;
import com.github.mtesmct.rieau.api.domain.repositories.SaveDossierException;

import java.io.InputStream;
import java.util.Optional;

public interface AjouterPieceJointeService {
    public Optional<PieceJointe> execute(DossierId id, String numero, InputStream is, String nom, String mimeType, long taille) throws PieceNonAJoindreException, AjouterPieceJointeException, AuthRequiredException, UserForbiddenException, UserInfoServiceException, SaveDossierException;
}
