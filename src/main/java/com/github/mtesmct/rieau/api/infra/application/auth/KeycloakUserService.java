package com.github.mtesmct.rieau.api.infra.application.auth;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.UserService;
import com.github.mtesmct.rieau.api.application.auth.UserServiceException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class KeycloakUserService implements UserService {

    @Override
    @SuppressWarnings("unchecked")
    public Optional<PersonnePhysique> findUserById(String id) throws UserServiceException {
        if (!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof KeycloakPrincipal))
            throw new UserServiceException("spring security principal is not instance of KeycloakPrincipal");
        KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
        AccessToken accessToken = session.getToken();
        Optional<PersonnePhysique> user = Optional.empty();
        user = Optional.ofNullable(new PersonnePhysique(id, accessToken.getEmail()));
        return user;
    }

}