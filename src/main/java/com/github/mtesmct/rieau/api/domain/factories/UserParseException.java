package com.github.mtesmct.rieau.api.domain.factories;

public class UserParseException extends Exception {
    private static final long serialVersionUID = 1L;

    private static String message(String userString) {
        return "La chaîne de caractères {" + userString + "} n'est pas compatible avec le format de User={...}";
    }

    public UserParseException(String userString) {
        super(message(userString));
    }

    public UserParseException(String userString, Throwable cause) {
        super(message(userString), cause);
    }
}