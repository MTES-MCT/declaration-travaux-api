package com.github.mtesmct.rieau.api.application.dossiers;

import java.util.Objects;

public class UserNotOwnerException extends Exception {
    private static final long serialVersionUID = 1L;

    public UserNotOwnerException(String userId, String fichierId) {
        super("L'utilisateur connecté {" + Objects.toString(userId) + "} n'est pas le pripréitaire du fichier {"
                + Objects.toString(fichierId) + "}");
    }
}