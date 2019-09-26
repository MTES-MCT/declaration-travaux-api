package com.github.mtesmct.rieau.api.application.auth;

public class UserInfoServiceException extends Exception {
    private static final long serialVersionUID = 1L;

    public UserInfoServiceException(String message) {
        super(message);
    }

    public UserInfoServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}