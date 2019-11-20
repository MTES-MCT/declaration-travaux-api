package com.github.mtesmct.rieau.api.application.dossiers;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.*;
import com.github.mtesmct.rieau.api.domain.repositories.SaveDossierException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;

public interface PrendreDecisionDossierService {
    public Optional<Dossier> execute(DossierId id, InputStream is, String nom, String mimeType, long taille) throws DossierNotFoundException, MairieForbiddenException,
            AuthRequiredException, UserForbiddenException, UserInfoServiceException, TypeStatutNotFoundException, StatutForbiddenException, FileNotFoundException, AjouterPieceJointeException, SaveDossierException;
}
