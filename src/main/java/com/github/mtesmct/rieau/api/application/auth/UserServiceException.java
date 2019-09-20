package com.github.mtesmct.rieau.api.application.auth;

public class UserServiceException extends Exception {
    private static final long serialVersionUID = 1L;

    public UserServiceException(String message) {
        super(message);
    }

    public UserServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}