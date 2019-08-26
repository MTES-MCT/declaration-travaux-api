package com.github.mtesmct.rieau.api.depositaire.domain.services;

public class DepotImportException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DepotImportException(String message) {
        super(message);
    }

    public DepotImportException(String message, Throwable cause) {
        super(message, cause);
    }
}