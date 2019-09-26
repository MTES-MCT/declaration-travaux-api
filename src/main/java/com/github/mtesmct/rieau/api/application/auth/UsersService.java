package com.github.mtesmct.rieau.api.application.auth;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;

public interface UsersService {
    public Optional<Personne> findUserById(String id);
}
