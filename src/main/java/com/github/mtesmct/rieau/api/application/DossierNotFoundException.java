package com.github.mtesmct.rieau.api.application;

public class DossierNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DossierNotFoundException(String message) {
        super(message);
    }

    public DossierNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}