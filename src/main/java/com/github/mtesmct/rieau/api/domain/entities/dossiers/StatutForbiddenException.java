package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumStatuts;

public class StatutForbiddenException extends Exception {
    private static final long serialVersionUID = 1L;

    public static String messageNonConsecutif(EnumStatuts statutDossier, EnumStatuts statutActuel) {
        return "Le statut de dossier {" + Objects.toString(statutDossier) + "} n'est pas consécutif au statut actuel {"
                + Objects.toString(statutActuel) + "}";
    }
    public static String messageDejaPresent(EnumStatuts statutDossier) {
        return "Le statut de dossier {" + Objects.toString(statutDossier) + "} est déjà présent dans l'historique }";
    }

    public StatutForbiddenException(EnumStatuts statutDossier) {
        super(messageDejaPresent(statutDossier));
    }

    public StatutForbiddenException(EnumStatuts statutDossier, Throwable cause) {
        super(messageDejaPresent(statutDossier), cause);
    }

    public StatutForbiddenException(EnumStatuts statutDossier, EnumStatuts statutActuel) {
        super(messageNonConsecutif(statutDossier, statutActuel));
    }

    public StatutForbiddenException(EnumStatuts statutDossier, EnumStatuts statutActuel, Throwable cause) {
        super(messageNonConsecutif(statutDossier,statutActuel), cause);
    }
}