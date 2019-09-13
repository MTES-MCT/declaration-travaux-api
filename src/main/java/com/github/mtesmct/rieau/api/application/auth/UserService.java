package com.github.mtesmct.rieau.api.application.auth;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;

public interface UserService {
    public Optional<PersonnePhysique> findUserById(String id) throws UserServiceException;
}
