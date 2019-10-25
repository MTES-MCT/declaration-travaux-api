package com.github.mtesmct.rieau.api.application.auth;

import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;

import java.util.Optional;

public interface UsersService {
    public Optional<Personne> findUserById(String id);
}
