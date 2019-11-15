package com.github.mtesmct.rieau.api.domain.factories;

public class AdresseParseException extends Exception {
    private static final long serialVersionUID = 1L;

    private static String message(String texte) {
        return "La chaîne de caractères {" + texte + "} n'est pas compatible avec le format de Adresse={...}";
    }

    public AdresseParseException(String texte) {
        super(message(texte));
    }

    public AdresseParseException(String texte, Throwable cause) {
        super(message(texte), cause);
    }
}