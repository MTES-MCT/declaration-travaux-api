package com.github.mtesmct.rieau.api.application;

public class DepotImportException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DepotImportException(String message) {
        super(message);
    }

    public DepotImportException(String message, Throwable cause) {
        super(message, cause);
    }
}