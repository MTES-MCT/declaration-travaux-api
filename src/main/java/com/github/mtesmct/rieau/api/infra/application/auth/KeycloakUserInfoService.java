package com.github.mtesmct.rieau.api.infra.application.auth;

import java.util.Map;

import com.github.mtesmct.rieau.api.application.auth.Roles;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Sexe;
import com.github.mtesmct.rieau.api.domain.factories.PersonneFactory;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
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

        @Autowired
        private PersonneFactory personneFactory;

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
                Sexe sexe = null;
                if (accessToken.getGender() != null)
                        sexe = Sexe.valueOf(accessToken.getGender());
                Personne user;
                Map<String, Object> otherClaims = accessToken.getOtherClaims();
                log.debug("otherClaims={}", otherClaims);
                if (token.getAccount().getRoles().contains(Roles.MAIRIE)) {
                        if (!otherClaims.containsKey("codePostal") || (String) accessToken.getOtherClaims().get("codePostal") == null
                                        || ((String) accessToken.getOtherClaims().get("codePostal")).isBlank())
                                throw new UserInfoServiceException(
                                                "L'utilisateur doit avoir un code postal de renseigné dans son profil {"
                                                                + Roles.MAIRIE + "}");
                }
                try {
                        user = this.personneFactory.creer(accessToken.getPreferredUsername(), accessToken.getEmail(),
                                        sexe, accessToken.getFamilyName(), accessToken.getGivenName(),
                                        accessToken.getBirthdate(),
                                        (String) accessToken.getOtherClaims().get("birthPostalCode"),
                                        (String) accessToken.getOtherClaims().get("codePostal"),
                                        (String) accessToken.getOtherClaims().get("numero"),
                                        (String) accessToken.getOtherClaims().get("voie"),
                                        (String) accessToken.getOtherClaims().get("lieuDit"),
                                        (String) accessToken.getOtherClaims().get("bp"),
                                        (String) accessToken.getOtherClaims().get("cedex"));
                } catch (CommuneNotFoundException e) {
                        throw new UserInfoServiceException(e);
                }
                log.debug("user={}", user);
                return user;
        }

}