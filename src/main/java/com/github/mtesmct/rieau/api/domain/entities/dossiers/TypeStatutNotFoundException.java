package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.Objects;

public class TypeStatutNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    private static String message(EnumStatuts statut) {
        return "Le type de statut de dossier {" + Objects.toString(statut) + "} est introuvable";
    }
    public TypeStatutNotFoundException(EnumStatuts statut) {
        super(message(statut));
    }

    public TypeStatutNotFoundException(EnumStatuts statut, Throwable cause) {
        super(message(statut), cause);
    }
}