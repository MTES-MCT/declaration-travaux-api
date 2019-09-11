package com.github.mtesmct.rieau.api.domain.services;

public class CerfaServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CerfaServiceException(String message) {
        super(message);
    }

    public CerfaServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}