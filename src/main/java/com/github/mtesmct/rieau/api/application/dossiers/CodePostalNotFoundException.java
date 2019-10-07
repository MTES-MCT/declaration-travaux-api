package com.github.mtesmct.rieau.api.application.dossiers;

public class CodePostalNotFoundException extends Exception {
    public static final String AUCUN_CODE_POSTAL_TROUVE_DANS_LE_FICHIER_PDF = "Aucun code postal trouvé dans le fichier pdf";
    private static final long serialVersionUID = 1L;

    public CodePostalNotFoundException() {
        super(AUCUN_CODE_POSTAL_TROUVE_DANS_LE_FICHIER_PDF);
    }

    public CodePostalNotFoundException(Throwable cause) {
        super(AUCUN_CODE_POSTAL_TROUVE_DANS_LE_FICHIER_PDF, cause);
    }
}