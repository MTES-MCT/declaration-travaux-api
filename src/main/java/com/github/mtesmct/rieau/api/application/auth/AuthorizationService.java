package com.github.mtesmct.rieau.api.application.auth;

import com.github.mtesmct.rieau.api.application.ApplicationService;

@ApplicationService
public class AuthorizationService {

    private AuthenticationService authenticationService;

    public AuthorizationService(AuthenticationService authenticationService) {
        if (authenticationService == null)
            throw new NullPointerException("Le service d'authentification ne peut pas être nul");
        this.authenticationService = authenticationService;
    }

    public void isDeposantAndBetaAuthorized() throws AuthRequiredException, UserForbiddenException {
        if (!this.authenticationService.isAuthenticaed())
            throw new AuthRequiredException();
        if (!this.authenticationService.isDeposant())
            throw new UserForbiddenException(Role.DEPOSANT);
        if (!this.authenticationService.isBeta())
            throw new UserForbiddenException(Role.BETA);
    }
}