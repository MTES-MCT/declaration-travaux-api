package com.github.mtesmct.rieau.api.infra.http.dossiers;

import lombok.Getter;

@Getter
public class JsonProjet{
    private JsonAdresse adresse;
    private boolean nouvelleConstruction;

    public JsonProjet(JsonAdresse adresse, boolean nouvelleConstruction) {
        this.adresse = adresse;
        this.nouvelleConstruction = nouvelleConstruction;
    }

}