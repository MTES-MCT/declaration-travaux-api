package com.github.mtesmct.rieau.api.depositaire.domain.entities;

public class DepotNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DepotNotFoundException(String message) {
        super(message);
    }

    public DepotNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}