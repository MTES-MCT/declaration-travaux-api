package com.github.mtesmct.rieau.api.application.auth;

import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;

import java.util.Optional;

public interface AuthenticationService {
    public boolean isAuthenticaed();
    public boolean isDeposant();
    public boolean isInstructeur();
    public boolean isMairie();
    public boolean isBeta();
    public Optional<Personne> user() throws UserInfoServiceException;
}