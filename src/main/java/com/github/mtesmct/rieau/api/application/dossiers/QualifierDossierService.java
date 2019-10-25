package com.github.mtesmct.rieau.api.application.dossiers;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.*;

import java.util.Optional;

public interface QualifierDossierService {
    public Optional<Dossier> execute(DossierId id) throws DossierNotFoundException, MairieForbiddenException,
            AuthRequiredException, UserForbiddenException, UserInfoServiceException, TypeStatutNotFoundException, StatutForbiddenException;
}
