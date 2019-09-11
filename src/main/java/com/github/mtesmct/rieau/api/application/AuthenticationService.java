package com.github.mtesmct.rieau.api.application;

public interface AuthenticationService {
    public boolean isAuthenticaed();
    public boolean isDemandeur();
    public boolean isInstructeur();
    public boolean isBeta();
    public String userId();
}