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

    public void isDeposantAndBetaAuthorized() {
        if (!this.authenticationService.isAuthenticaed())
            throw new IllegalAccessError("L'utilisateur doit être authentifié.");
        if (!this.authenticationService.isDeposant())
            throw new IllegalAccessError("L'utilisateur doit avoir le rôle de deposant.");
        if (!this.authenticationService.isBeta())
            throw new IllegalAccessError("L'utilisateur doit avoir le rôle de beta testeur.");
    }
}