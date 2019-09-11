package com.github.mtesmct.rieau.api.infra.security;

import com.github.mtesmct.rieau.api.application.AuthenticationService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SpringSecurityAuthenticationService implements AuthenticationService {

    @Override
    public boolean isAuthenticaed() {
        return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }

    @Override
    public boolean isDemandeur() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().filter(a -> a.getAuthority().equals("ROLE_demandeur")).findAny().isPresent();
    }

    @Override
    public boolean isInstructeur() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().filter(a -> a.getAuthority().equals("ROLE_instructeur")).findAny().isPresent();
    }

    @Override
    public boolean isBeta() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().filter(a -> a.getAuthority().equals("ROLE_beta")).findAny().isPresent();
    }

    @Override
    public String userId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}