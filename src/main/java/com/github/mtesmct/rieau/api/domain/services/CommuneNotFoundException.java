package com.github.mtesmct.rieau.api.domain.services;

public class CommuneNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public static String message(String codPostal) {
        return "La commune avec le code postal {" + codPostal + "} est introuvable";
    }

    public CommuneNotFoundException(String codPostal) {
        super(message(codPostal));
    }

    public CommuneNotFoundException(String codPostal, Throwable cause) {
        super(message(codPostal), cause);
    }
}