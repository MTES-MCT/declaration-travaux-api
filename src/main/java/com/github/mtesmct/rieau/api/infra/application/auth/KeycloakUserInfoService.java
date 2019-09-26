package com.github.mtesmct.rieau.api.infra.application.auth;

import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnProperty(prefix = "keycloak", name = "enabled", havingValue = "true")
@Primary
@Slf4j
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
                log.debug("Keycloak user info: ");
                log.debug("user.id={}", accessToken.getPreferredUsername());
                log.debug("user.email={}", accessToken.getEmail());
                log.debug("user.birthdate={}", accessToken.getBirthdate());
                log.debug("user.gender={}", accessToken.getGender());
                log.debug("user.family_name={}", accessToken.getFamilyName());
                log.debug("user.given_name={}", accessToken.getGivenName());
                Personne user = new Personne(accessToken.getPreferredUsername(), accessToken.getEmail());
                log.debug("user={}", user);
                return user;
        }

}