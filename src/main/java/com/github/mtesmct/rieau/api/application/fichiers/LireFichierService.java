package com.github.mtesmct.rieau.api.application.fichiers;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.DossierNotFoundException;
import com.github.mtesmct.rieau.api.application.dossiers.FichierNotFoundException;
import com.github.mtesmct.rieau.api.application.dossiers.UserNotOwnerException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.MairieForbiddenException;

import java.util.Optional;

public interface LireFichierService {
    public Optional<Fichier> execute(FichierId id) throws FichierNotFoundException, UserForbiddenException, AuthRequiredException, UserInfoServiceException, UserNotOwnerException, DossierNotFoundException, MairieForbiddenException;
}