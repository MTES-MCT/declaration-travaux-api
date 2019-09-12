package com.github.mtesmct.rieau.api.infra.security;

import com.github.mtesmct.rieau.api.application.auth.AuthenticationService;
import com.github.mtesmct.rieau.api.application.auth.Role;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SpringSecurityAuthenticationService implements AuthenticationService {

    @Override
    public boolean isAuthenticaed() {
        return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }

    @Override
    public boolean isDeposant() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .filter(a -> a.getAuthority().equals("ROLE_" + Role.DEMANDEUR.toString())).findAny().isPresent();
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
    public String userId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}