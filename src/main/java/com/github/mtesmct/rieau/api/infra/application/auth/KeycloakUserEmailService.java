package com.github.mtesmct.rieau.api.infra.application.auth;

import com.github.mtesmct.rieau.api.application.auth.UserServiceException;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Profile("staging")
@Primary
public class KeycloakUserEmailService implements UserEmailService {

    @Override
    @SuppressWarnings("unchecked")
    public String email() throws UserServiceException {
        if (!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof KeycloakPrincipal))
            throw new UserServiceException("spring security principal is not instance of KeycloakPrincipal");
        KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        if (principal == null)
            throw new UserServiceException("keycloak principal is null");
        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
        if (session == null)
            throw new UserServiceException("keycloak context is null");
        AccessToken accessToken = session.getToken();
        if (accessToken == null)
            throw new UserServiceException("keycloak access token is null");
        return accessToken.getEmail();
    }

}