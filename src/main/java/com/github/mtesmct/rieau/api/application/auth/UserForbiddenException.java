package com.github.mtesmct.rieau.api.application.auth;

import java.util.Arrays;
import java.util.Objects;

public class UserForbiddenException extends Exception {
    private static final long serialVersionUID = 1L;

    public UserForbiddenException(String ...roles) {
        super("L'utilisateur connecté n'a pas les profils requis {" + Objects.toString(Arrays.toString(roles)) + "}");
    }
}