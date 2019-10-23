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

    public void isUserAuthorized() throws AuthRequiredException, UserForbiddenException {
        if (!this.authenticationService.isAuthenticaed())
            throw new AuthRequiredException();
        if (!this.authenticationService.isDeposant() && !this.authenticationService.isMairie() && !this.authenticationService.isInstructeur())
            throw new UserForbiddenException(new String[]{Roles.DEPOSANT, Roles.MAIRIE, Roles.INSTRUCTEUR});
    }

    public void isDeposantOrMairieAndBetaAuthorized() throws AuthRequiredException, UserForbiddenException {
        if (!this.authenticationService.isAuthenticaed())
            throw new AuthRequiredException();
        if (!this.authenticationService.isDeposant() && !this.authenticationService.isMairie())
            throw new UserForbiddenException(new String[]{Roles.DEPOSANT, Roles.MAIRIE});
        if (!this.authenticationService.isBeta())
            throw new UserForbiddenException(new String[]{Roles.BETA});
    }

    public void isDeposantOrInstructeurAndBetaAuthorized() throws AuthRequiredException, UserForbiddenException {
        if (!this.authenticationService.isAuthenticaed())
            throw new AuthRequiredException();
        if (!this.authenticationService.isDeposant() && !this.authenticationService.isInstructeur())
            throw new UserForbiddenException(new String[]{Roles.DEPOSANT, Roles.INSTRUCTEUR});
        if (this.authenticationService.isDeposant() && !this.authenticationService.isBeta())
            throw new UserForbiddenException(new String[]{Roles.BETA});
    }

	public void isMairieAndBetaAuthorized() throws AuthRequiredException, UserForbiddenException {
        if (!this.authenticationService.isAuthenticaed())
            throw new AuthRequiredException();
        if (!this.authenticationService.isMairie())
            throw new UserForbiddenException(new String[]{Roles.MAIRIE});
        if (!this.authenticationService.isBeta())
            throw new UserForbiddenException(new String[]{Roles.BETA});
	}

	public void isDeposantAndBetaAuthorized() throws AuthRequiredException, UserForbiddenException {
        if (!this.authenticationService.isAuthenticaed())
            throw new AuthRequiredException();
        if (!this.authenticationService.isDeposant())
            throw new UserForbiddenException(new String[]{Roles.DEPOSANT});
        if (!this.authenticationService.isBeta())
            throw new UserForbiddenException(new String[]{Roles.BETA});
    }

	public void isInstructeurAuthorized() throws AuthRequiredException, UserForbiddenException {
        if (!this.authenticationService.isAuthenticaed())
            throw new AuthRequiredException();
        if (!this.authenticationService.isInstructeur())
            throw new UserForbiddenException(new String[]{Roles.INSTRUCTEUR});
    }
}