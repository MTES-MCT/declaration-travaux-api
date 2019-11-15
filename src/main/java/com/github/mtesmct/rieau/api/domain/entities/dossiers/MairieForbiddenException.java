package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.personnes.User;

public class MairieForbiddenException extends Exception {
    private static final long serialVersionUID = 1L;

    private static String message(User user) {
        return "L'utilisateur {" + Objects.toString(user)
                + "} n'est pas autorisé car il n'en est pas la mairie.";
    }

    public MairieForbiddenException(User user) {
        super(message(user));
    }

    public MairieForbiddenException(User user, Throwable cause) {
        super(message(user),
                cause);
    }
}