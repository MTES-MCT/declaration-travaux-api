package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.Objects;

public class TypeDossierNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    private static String message(EnumTypes type) {
        return "Le type de dossier {" + Objects.toString(type) + "} est introuvable";
    }

    public TypeDossierNotFoundException(EnumTypes type) {
        super(message(type));
    }

    public TypeDossierNotFoundException(EnumTypes type, Throwable cause) {
        super(message(type), cause);
    }
}