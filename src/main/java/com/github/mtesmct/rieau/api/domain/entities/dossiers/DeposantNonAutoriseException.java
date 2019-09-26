package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;

public class DeposantNonAutoriseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DeposantNonAutoriseException(Personne user) {
        super("L'utilisateur {" + user != null ? user.toString() : ""
        + "} n'est pas autorisé à ajouter une pièce jointe à ce dossier car il n'en est pas le déposant.");
    }

    public DeposantNonAutoriseException(Personne user, Throwable cause) {
        super("L'utilisateur {" + user != null ? user.toString() : ""
        + "} n'est pas autorisé à ajouter une pièce jointe à ce dossier car il n'en est pas le déposant.", cause);
    }
}