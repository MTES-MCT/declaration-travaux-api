package com.github.mtesmct.rieau.api.application.dossiers;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.*;

import java.util.Optional;

public interface InstruireDossierService {
    public Optional<Dossier> execute(DossierId id) throws DossierNotFoundException, InstructeurForbiddenException,
            AuthRequiredException, UserForbiddenException, UserInfoServiceException, TypeStatutNotFoundException, StatutForbiddenException;
}
