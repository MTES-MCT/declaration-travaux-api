package com.github.mtesmct.rieau.api.application.dossiers;

import java.util.Objects;

public class UserNotOwnerException extends Exception {
    private static final long serialVersionUID = 1L;

    public final static String message(String userId, String fichierId) {
        return "L'utilisateur connecté {" + Objects.toString(userId) + "} n'est pas le propriétaire du fichier {"
                + Objects.toString(fichierId) + "}";
    }
    public UserNotOwnerException(String userId, String fichierId) {
        super(message(userId, fichierId));
    }
    public UserNotOwnerException(String userId, String fichierId, Throwable e) {
        super(message(userId, fichierId), e);
    }
}