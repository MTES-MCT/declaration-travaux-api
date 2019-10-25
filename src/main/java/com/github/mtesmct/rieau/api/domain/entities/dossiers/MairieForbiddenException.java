package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;

import java.util.Objects;

public class MairieForbiddenException extends Exception {
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