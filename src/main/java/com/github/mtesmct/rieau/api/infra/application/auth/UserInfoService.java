package com.github.mtesmct.rieau.api.infra.application.auth;

import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;

public interface UserInfoService {
    public Personne user() throws UserInfoServiceException;
}
