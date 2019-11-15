package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.personnes.User;

public class DeposantForbiddenException extends Exception {
    private static final long serialVersionUID = 1L;

    private static String message(User user) {
        return "L'utilisateur {" + Objects.toString(user)
                + "} n'est pas autorisé car il n'en est pas le déposant.";
    }

    public DeposantForbiddenException(User user) {
        super(message(user));
    }

    public DeposantForbiddenException(User user, Throwable cause) {
        super(message(user),
                cause);
    }
}