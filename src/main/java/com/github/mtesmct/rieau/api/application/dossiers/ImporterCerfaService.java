package com.github.mtesmct.rieau.api.application.dossiers;

import java.io.InputStream;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceNonAJoindreException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossierNotFoundException;

public interface ImporterCerfaService {
    public Optional<Dossier> execute(InputStream is, String nom, String mimeType, long taille) throws DossierImportException, AuthRequiredException, UserForbiddenException, UserInfoServiceException, StatutForbiddenException, TypeStatutNotFoundException, PieceNonAJoindreException, TypeDossierNotFoundException;
}
