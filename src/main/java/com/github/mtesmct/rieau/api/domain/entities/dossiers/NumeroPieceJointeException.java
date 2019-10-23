package com.github.mtesmct.rieau.api.domain.entities.dossiers;

public class NumeroPieceJointeException extends Exception {
    private static final long serialVersionUID = 1L;
    public static final String RG1_NUMEROS_CERFA_DECISION = "Le numéro de la pièce jointe doit être différent de {0,d}. Les numéros {0,d} sont réservés au CERFA et à la décision.";

    public NumeroPieceJointeException() {
        super(RG1_NUMEROS_CERFA_DECISION);
    }

    public NumeroPieceJointeException(Throwable cause) {
        super(RG1_NUMEROS_CERFA_DECISION, cause);
    }
}