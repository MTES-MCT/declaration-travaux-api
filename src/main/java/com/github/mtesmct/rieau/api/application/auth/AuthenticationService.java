package com.github.mtesmct.rieau.api.application.auth;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;

public interface AuthenticationService {
    public boolean isAuthenticaed();
    public boolean isDeposant();
    public boolean isInstructeur();
    public boolean isBeta();
    public Optional<PersonnePhysique> user();
}