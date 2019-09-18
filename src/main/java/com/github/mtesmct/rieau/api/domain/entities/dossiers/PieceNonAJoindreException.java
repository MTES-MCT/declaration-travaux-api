package com.github.mtesmct.rieau.api.domain.entities.dossiers;

public class PieceNonAJoindreException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PieceNonAJoindreException(CodePieceJointe code) {
        super("La pièce " + code.toString()
        + " ne fait pas partie des pièces à joindre.");
    }

    public PieceNonAJoindreException(CodePieceJointe code, Throwable cause) {
        super("La pièce " + code.toString()
        + " ne fait pas partie des pièces à joindre.", cause);
    }
}