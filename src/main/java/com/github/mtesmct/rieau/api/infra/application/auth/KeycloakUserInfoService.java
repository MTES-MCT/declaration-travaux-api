package com.github.mtesmct.rieau.api.infra.application.auth;

import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Sexe;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Profile("staging")
@Primary
public class KeycloakUserInfoService implements UserInfoService {

        @Override
        public Personne user() throws UserInfoServiceException {
                SecurityContext context = SecurityContextHolder.getContext();
                if (!KeycloakAuthenticationToken.class.isAssignableFrom(context.getAuthentication().getClass()))
                        throw new UserInfoServiceException("Expected a KeycloakAuthenticationToken, but found "
                                        + context.getAuthentication());
                KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) context.getAuthentication();
                KeycloakSecurityContext keycloakSecurityContext = token.getAccount().getKeycloakSecurityContext();
                if (keycloakSecurityContext == null)
                        throw new UserInfoServiceException("keycloakSecurityContext is null");
                AccessToken accessToken = keycloakSecurityContext.getToken();
                if (accessToken == null)
                        throw new UserInfoServiceException("accessToken is null");
                return new Personne(accessToken.getPreferredUsername(), accessToken.getEmail(),
                                accessToken.getFamilyName(), accessToken.getName(),
                                Sexe.valueOf(accessToken.getGender()), null);
        }

}