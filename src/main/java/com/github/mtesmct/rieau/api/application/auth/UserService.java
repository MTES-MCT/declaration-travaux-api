package com.github.mtesmct.rieau.api.application.auth;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;

public interface UserService {
    public Optional<Personne> findUserById(String id);
}
