package com.github.mtesmct.rieau.api.application.dossiers;

import java.io.InputStream;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;

public interface ImporterCerfaService {
    public Optional<Dossier> execute(InputStream is, String nom, String mimeType, long taille) throws DossierImportException, AuthRequiredException, UserForbiddenException, UserInfoServiceException;
}
