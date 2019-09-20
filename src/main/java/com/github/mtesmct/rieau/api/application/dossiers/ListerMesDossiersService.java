package com.github.mtesmct.rieau.api.application.dossiers;

import java.util.List;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserServiceException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;

public interface ListerMesDossiersService {
    public List<Dossier> execute() throws AuthRequiredException, UserForbiddenException, UserServiceException;
}
