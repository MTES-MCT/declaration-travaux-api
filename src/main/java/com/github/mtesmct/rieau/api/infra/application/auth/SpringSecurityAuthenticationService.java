package com.github.mtesmct.rieau.api.infra.application.auth;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthenticationService;
import com.github.mtesmct.rieau.api.application.auth.Role;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SpringSecurityAuthenticationService implements AuthenticationService {

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public boolean isAuthenticaed() {
        return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }

    @Override
    public boolean isDeposant() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .filter(a -> a.getAuthority().equals("ROLE_" + Role.DEPOSANT.toString())).findAny().isPresent();
    }

    @Override
    public boolean isInstructeur() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .filter(a -> a.getAuthority().equals("ROLE_" + Role.INSTRUCTEUR.toString())).findAny().isPresent();
    }

    @Override
    public boolean isBeta() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .filter(a -> a.getAuthority().equals("ROLE_" + Role.BETA.toString())).findAny().isPresent();
    }

    @Override
    public Optional<Personne> user() throws UserInfoServiceException {
        return Optional.ofNullable(this.userInfoService.user());
    }

}