package com.github.mtesmct.rieau.api.application.dossiers;

public class DossierImportException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DossierImportException(String message) {
        super(message);
    }

    public DossierImportException(String message, Throwable cause) {
        super(message, cause);
    }
}