package com.github.mtesmct.rieau.api.infra.application.auth;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.UserService;
import com.github.mtesmct.rieau.api.application.auth.UserServiceException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestComponent
@Profile("!staging")
@Primary
public class MockUserService implements UserService {

    public static final String EMAIL_DOMAIN = "@monfai.fr";

    @Override
    public Optional<PersonnePhysique> findUserById(String id) throws UserServiceException {
        Optional<PersonnePhysique> user = Optional.empty();
        user = Optional.ofNullable(new PersonnePhysique(id, id + EMAIL_DOMAIN));
        return user;
    }

}