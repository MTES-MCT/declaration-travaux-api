package com.github.mtesmct.rieau.api.infra.http;

import lombok.Getter;

@Getter
public class JsonDossier {
    private String id;
    private String type;
    private String statut;
    private String date;

    public JsonDossier(String id, String type, String statut, String date) {
        this.id = id;
        this.type = type;
        this.statut = statut;
        this.date = date;
    }

}