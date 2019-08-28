package com.github.mtesmct.rieau.api.domain.adapters;

public class CerfaAdapterException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CerfaAdapterException(String message) {
        super(message);
    }

    public CerfaAdapterException(String message, Throwable cause) {
        super(message, cause);
    }
}