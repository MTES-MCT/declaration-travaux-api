package com.github.mtesmct.rieau.api.application.auth;

import java.util.Objects;

public class UserNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String userId) {
        super("L'utilisateur connecté {" + Objects.toString(userId) + "} non trouvé dans la base");
    }
}