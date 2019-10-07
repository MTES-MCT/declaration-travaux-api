package com.github.mtesmct.rieau.api.application.dossiers;

public class FormValueCerfaNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    public static String message(String nomChamp) {
        return "La valeur du champ formulaire {" + nomChamp + "} est introuvable";
    }

    public FormValueCerfaNotFoundException(String nomChamp) {
        super(message(nomChamp));
    }

    public FormValueCerfaNotFoundException(String nomChamp, Throwable cause) {
        super(message(nomChamp), cause);
    }
}