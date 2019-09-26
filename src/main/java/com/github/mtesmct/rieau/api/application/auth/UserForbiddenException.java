package com.github.mtesmct.rieau.api.application.auth;

import java.util.Objects;

public class UserForbiddenException extends Exception {
    private static final long serialVersionUID = 1L;

    public UserForbiddenException(String role) {
        super("L'utilisateur connecté n'a pas le profil requis {" + Objects.toString(role) + "}");
    }
}