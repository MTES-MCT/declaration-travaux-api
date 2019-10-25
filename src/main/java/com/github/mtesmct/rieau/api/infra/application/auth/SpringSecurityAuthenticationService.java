package com.github.mtesmct.rieau.api.infra.application.auth;

import com.github.mtesmct.rieau.api.application.auth.AuthenticationService;
import com.github.mtesmct.rieau.api.application.auth.Roles;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SpringSecurityAuthenticationService implements AuthenticationService {

    @Autowired
    private UserInfoService userInfoService;

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public boolean isAuthenticaed() {
        return getAuthentication() != null && getAuthentication().isAuthenticated();
    }

    @Override
    public boolean isDeposant() {
        return getAuthentication() != null && getAuthentication().getAuthorities().stream()
                .filter(a -> a.getAuthority().equals("ROLE_" + Roles.DEPOSANT)).findAny().isPresent();
    }

    @Override
    public boolean isInstructeur() {
        return getAuthentication() != null && getAuthentication().getAuthorities().stream()
                .filter(a -> a.getAuthority().equals("ROLE_" + Roles.INSTRUCTEUR)).findAny().isPresent();
    }

    @Override
    public boolean isMairie() {
        return getAuthentication() != null && getAuthentication().getAuthorities().stream()
                .filter(a -> a.getAuthority().equals("ROLE_" + Roles.MAIRIE)).findAny().isPresent();
    }

    @Override
    public boolean isBeta() {
        return getAuthentication() != null && getAuthentication().getAuthorities().stream()
                .filter(a -> a.getAuthority().equals("ROLE_" + Roles.BETA)).findAny().isPresent();
    }

    @Override
    public Optional<Personne> user() throws UserInfoServiceException {
        return Optional.ofNullable(this.userInfoService.user());
    }

}