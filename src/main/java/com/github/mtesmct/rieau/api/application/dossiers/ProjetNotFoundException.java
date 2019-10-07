package com.github.mtesmct.rieau.api.application.dossiers;

public class ProjetNotFoundException extends Exception {
    public static final String AUCUN_PROJET_TROUVE_DANS_LE_FICHIER_PDF = "Aucun projet trouvé dans le fichier pdf";
    private static final long serialVersionUID = 1L;

    public ProjetNotFoundException() {
        super(AUCUN_PROJET_TROUVE_DANS_LE_FICHIER_PDF);
    }

    public ProjetNotFoundException(Throwable cause) {
        super(AUCUN_PROJET_TROUVE_DANS_LE_FICHIER_PDF, cause);
    }
}