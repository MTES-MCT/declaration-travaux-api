package com.github.mtesmct.rieau.api.domain.entities.dossiers;

public class NumeroPieceJointeException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public static final String RG1_NUMERO_CERFA_O = "Le numéro de la pièce jointe doit être différent de 0. Le numéro 0 est réservé au CERFA.";

    public NumeroPieceJointeException() {
        super(RG1_NUMERO_CERFA_O);
    }

    public NumeroPieceJointeException(Throwable cause) {
        super(RG1_NUMERO_CERFA_O, cause);
    }
}