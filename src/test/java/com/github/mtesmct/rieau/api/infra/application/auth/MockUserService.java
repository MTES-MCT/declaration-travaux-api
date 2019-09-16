package com.github.mtesmct.rieau.api.infra.application.auth;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.UserService;
import com.github.mtesmct.rieau.api.application.auth.UserServiceException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;

@TestComponent
@Primary
public class MockUserService implements UserService {

    public static final String EMAIL_DOMAIN = "@monfai.fr";

    @Override
    public Optional<Personne> findUserById(String id) throws UserServiceException {
        Optional<Personne> user = Optional.empty();
        user = Optional.ofNullable(new Personne(id, id + EMAIL_DOMAIN));
        return user;
    }

}