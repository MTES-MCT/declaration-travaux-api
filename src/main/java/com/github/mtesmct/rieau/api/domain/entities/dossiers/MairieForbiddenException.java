package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;

public class MairieForbiddenException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static String message(Personne user) {
        return "L'utilisateur {" + Objects.toString(user)
                + "} n'est pas autorisé car il n'en est pas la mairie.";
    }

    public MairieForbiddenException(Personne user) {
        super(message(user));
    }

    public MairieForbiddenException(Personne user, Throwable cause) {
        super(message(user),
                cause);
    }
}