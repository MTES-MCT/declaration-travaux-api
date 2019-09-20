package com.github.mtesmct.rieau.api.infra.application.auth;

import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.core.context.SecurityContextHolder;

@TestComponent
public class MockUserInfoService implements UserInfoService {

    @Override
    public Personne user() {
        return new Personne(SecurityContextHolder.getContext().getAuthentication().getName(), SecurityContextHolder.getContext().getAuthentication().getName() + MockUsersService.EMAIL_DOMAIN);
    }


}