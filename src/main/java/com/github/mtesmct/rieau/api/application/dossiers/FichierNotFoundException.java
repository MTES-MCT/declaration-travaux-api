package com.github.mtesmct.rieau.api.application.dossiers;

public class FichierNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public static String message(String id) {
        return "Le fichier avec l'id {" + id + "} est introuvable";
    }

    public FichierNotFoundException(String id) {
        super(message(id));
    }

    public FichierNotFoundException(String id, Throwable cause) {
        super(message(id), cause);
    }
}