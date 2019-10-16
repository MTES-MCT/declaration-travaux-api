package com.github.mtesmct.rieau.api.application.dossiers;

import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;

public class TypeNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    private static String message(EnumTypes type) {
        return "Le type de dossier {" + Objects.toString(type) + "} est introuvable";
    }

    public TypeNotFoundException(EnumTypes type) {
        super(message(type));
    }

    public TypeNotFoundException(EnumTypes type, Throwable cause) {
        super(message(type), cause);
    }
}