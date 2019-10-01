package com.github.mtesmct.rieau.api.infra.application.auth;

import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ConditionalOnProperty(prefix = "keycloak", name = "enabled", havingValue = "false")
public class BasicUserInfoService implements UserInfoService {

    @Override
    public Personne user() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String nom = authentication != null ? authentication.getName() : "null";
        Personne user = new Personne(nom, nom + BasicUsersService.EMAIL_DOMAIN);
        log.debug("user={}", user);
        return user;
    }

}