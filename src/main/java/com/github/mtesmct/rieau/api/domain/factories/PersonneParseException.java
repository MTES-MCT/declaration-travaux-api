package com.github.mtesmct.rieau.api.domain.factories;

public class PersonneParseException extends Exception {
    private static final long serialVersionUID = 1L;

    private static String message(String personneString) {
        return "La chaîne de caractères {" + personneString + "} n'est pas compatible avec le format de Personne={...}";
    }

    public PersonneParseException(String personneString) {
        super(message(personneString));
    }

    public PersonneParseException(String personneString, Throwable cause) {
        super(message(personneString), cause);
    }
}