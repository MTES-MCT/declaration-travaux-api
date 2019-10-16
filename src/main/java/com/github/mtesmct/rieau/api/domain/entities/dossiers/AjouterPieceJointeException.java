package com.github.mtesmct.rieau.api.domain.entities.dossiers;

public class AjouterPieceJointeException extends Exception {
    private static final long serialVersionUID = 1L;

    public AjouterPieceJointeException(Throwable cause) {
        super(cause);
    }

    public AjouterPieceJointeException(String message) {
        super(message);
    }

    public AjouterPieceJointeException(String message, Throwable cause) {
        super(message, cause);
    }
}