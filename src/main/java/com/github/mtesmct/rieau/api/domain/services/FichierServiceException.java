package com.github.mtesmct.rieau.api.domain.services;

public class FichierServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public FichierServiceException(String message) {
        super(message);
    }

    public FichierServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public FichierServiceException(Throwable cause) {
        super(cause);
    }
}