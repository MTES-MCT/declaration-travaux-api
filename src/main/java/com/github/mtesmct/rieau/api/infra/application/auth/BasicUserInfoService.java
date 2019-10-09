package com.github.mtesmct.rieau.api.infra.application.auth;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.auth.UserNotFoundException;
import com.github.mtesmct.rieau.api.application.auth.UsersService;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ConditionalOnProperty(prefix = "keycloak", name = "enabled", havingValue = "false")
public class BasicUserInfoService implements UserInfoService {

    @Autowired
    private UsersService usersService;

    @Override
    public Personne user() throws UserInfoServiceException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication != null ? authentication.getName() : "null";
        Optional<Personne> user = this.usersService.findUserById(userId);
        if (user.isEmpty())
            throw new UserInfoServiceException(new UserNotFoundException(userId));
        log.debug("user={}", user.get());
        return user.get();
    }

}