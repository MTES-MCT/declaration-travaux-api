package com.github.mtesmct.rieau.api.application.dossiers;

import java.util.List;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;

public interface ListerDossiersService {
    public List<Dossier> execute() throws AuthRequiredException, UserForbiddenException, UserInfoServiceException;
}
