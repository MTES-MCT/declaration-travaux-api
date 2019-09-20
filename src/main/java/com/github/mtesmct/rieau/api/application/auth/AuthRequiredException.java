package com.github.mtesmct.rieau.api.application.auth;

public class AuthRequiredException extends Exception {
    private static final long serialVersionUID = 1L;

    public AuthRequiredException() {
        super("L'utilisateur doit être authentifié.");
    }
}