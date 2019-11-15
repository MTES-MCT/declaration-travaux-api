package com.github.mtesmct.rieau.api.application.auth;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.personnes.User;

public interface UsersService {
    public Optional<User> findUserById(String id);
}
