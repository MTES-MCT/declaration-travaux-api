package com.github.mtesmct.rieau.api.application.dossiers;

public class DossierNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public DossierNotFoundException(String id) {
        super("Le dossier avec l'id {" + id + "} est introuvable");
    }

    public DossierNotFoundException(String id, Throwable cause) {
        super("Le dossier avec l'id {" + id + "} est introuvable", cause);
    }
}