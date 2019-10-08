package com.github.mtesmct.rieau.api.infra.http.dossiers;

import lombok.Getter;

@Getter
public class JsonProjet{
    private JsonAdresse adresse;
    private boolean nouvelleConstruction;
    private boolean lotissement;

    public JsonProjet(JsonAdresse adresse, boolean nouvelleConstruction, boolean lotissement) {
        this.adresse = adresse;
        this.nouvelleConstruction = nouvelleConstruction;
        this.lotissement = lotissement;
    }

}