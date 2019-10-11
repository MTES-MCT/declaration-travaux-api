package com.github.mtesmct.rieau.api.application.dossiers;

import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;

public class DossierNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    private static String messageId(DossierId id) {
        return "Le dossier avec l'id {" + Objects.toString(id) + "} est introuvable";
    }
    private static String messageFichierId(FichierId id) {
        return "Le dossier rattaché au fichier dont l'id est {" + Objects.toString(id) + "} est introuvable";
    }

    public DossierNotFoundException(DossierId id) {
        super(messageId(id));
    }

    public DossierNotFoundException(FichierId id) {
        super(messageFichierId(id));
    }

    public DossierNotFoundException(DossierId id, Throwable cause) {
        super(messageId(id), cause);
    }

    public DossierNotFoundException(FichierId id, Throwable cause) {
        super(messageFichierId(id), cause);
    }
}