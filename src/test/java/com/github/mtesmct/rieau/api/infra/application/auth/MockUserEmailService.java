package com.github.mtesmct.rieau.api.infra.application.auth;

import com.github.mtesmct.rieau.api.application.auth.UserServiceException;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.core.context.SecurityContextHolder;

@TestComponent
public class MockUserEmailService implements UserEmailService {

    @Override
    public String email() throws UserServiceException {        
        return SecurityContextHolder.getContext().getAuthentication().getName() + MockUserService.EMAIL_DOMAIN;
    }

}