package com.github.mtesmct.rieau.api.infra.application.auth;

import java.util.Map;
import java.util.Objects;

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

        private void checkUserAttribute(KeycloakAuthenticationToken token, AccessToken accessToken,
                        Map<String, Object> otherClaims, String userRole, String attribute)
                        throws UserInfoServiceException {
                if (token.getAccount().getRoles().stream().anyMatch(role -> Objects.equals(role, userRole))) {
                        if (!otherClaims.containsKey(attribute)
                                        || (String) accessToken.getOtherClaims().get(attribute) == null
                                        || ((String) accessToken.getOtherClaims().get(attribute)).isBlank())
                                throw new UserInfoServiceException("L'utilisateur doit avoir l'attribut " + attribute
                                                + " de renseigné dans son profil {" + userRole + "}");
                }
        }

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
                log.debug("accessToken.type={}", accessToken.getType());
                log.debug("accessToken.scope={}", accessToken.getScope());
                Sexe sexe = null;
                if (accessToken.getGender() != null)
                        sexe = Sexe.valueOf(accessToken.getGender());
                Personne user;
                Map<String, Object> otherClaims = accessToken.getOtherClaims();
                log.debug("otherClaims={}", otherClaims);
                checkUserAttribute(token, accessToken, otherClaims, Roles.MAIRIE, "codePostal");
                checkUserAttribute(token, accessToken, otherClaims, Roles.INSTRUCTEUR, "codePostal");
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