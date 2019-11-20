package com.github.mtesmct.rieau.api.domain.repositories;

import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;

public class SaveDossierException extends Exception {
    private static final long serialVersionUID = 1L;

    private static String message(Dossier dossier) {
        return "Erreur de persistence du dossier {" + Objects.toString(dossier) + "}.";
    }

    public SaveDossierException(Dossier dossier) {
        super(message(dossier));
    }

    public SaveDossierException(Dossier dossier, Throwable cause) {
        super(message(dossier), cause);
    }
}