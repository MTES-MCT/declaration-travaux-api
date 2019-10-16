package com.github.mtesmct.rieau.api.domain.entities.dossiers;

public class PieceNonAJoindreException extends Exception {
    private static final long serialVersionUID = 1L;

    private static String message(CodePieceJointe code) {
        return "La pièce " + code.toString()
        + " ne fait pas partie des pièces à joindre.";
    }

    public PieceNonAJoindreException(CodePieceJointe code) {
        super(message(code));
    }

    public PieceNonAJoindreException(CodePieceJointe code, Throwable cause) {
        super(message(code), cause);
    }
}