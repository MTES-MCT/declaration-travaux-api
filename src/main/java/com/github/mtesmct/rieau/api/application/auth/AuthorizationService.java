package com.github.mtesmct.rieau.api.application.auth;

import com.github.mtesmct.rieau.api.application.ApplicationService;

@ApplicationService
public class AuthorizationService {
    
    private AuthenticationService authenticationService;

    public void isDemandeurAndBetaAuthorized() {
        if (!this.authenticationService.isAuthenticaed())
            throw new IllegalAccessError("L'utilisateur doit être authentifié.");
        if (!this.authenticationService.isDemandeur())
            throw new IllegalAccessError("L'utilisateur doit avoir le rôle de demandeur.");
        if (!this.authenticationService.isBeta())
            throw new IllegalAccessError("L'utilisateur doit avoir le rôle de beta testeur.");
    }
}