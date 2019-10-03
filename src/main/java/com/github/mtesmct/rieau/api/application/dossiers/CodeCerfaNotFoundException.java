package com.github.mtesmct.rieau.api.application.dossiers;

public class CodeCerfaNotFoundException extends Exception {
    public static final String AUCUN_CODE_CERFA_TROUVE_DANS_LE_FICHIER_PDF = "Aucun code CERFA trouvé dans le fichier pdf";
    private static final long serialVersionUID = 1L;

    public CodeCerfaNotFoundException() {
        super(AUCUN_CODE_CERFA_TROUVE_DANS_LE_FICHIER_PDF);
    }

    public CodeCerfaNotFoundException(Throwable cause) {
        super(AUCUN_CODE_CERFA_TROUVE_DANS_LE_FICHIER_PDF, cause);
    }
}