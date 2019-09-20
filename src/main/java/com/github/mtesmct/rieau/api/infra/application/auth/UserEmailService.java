package com.github.mtesmct.rieau.api.infra.application.auth;

import com.github.mtesmct.rieau.api.application.auth.UserServiceException;

public interface UserEmailService {
    public String email() throws UserServiceException;
}
