package com.github.mtesmct.rieau.api.infra.http.dossiers;
import lombok.Getter;

@Getter
public class JsonTypeDossier {
    private String id;
    private String libelle;

    public JsonTypeDossier(String id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }
}