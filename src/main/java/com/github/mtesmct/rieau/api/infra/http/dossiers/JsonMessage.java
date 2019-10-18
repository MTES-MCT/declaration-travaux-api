package com.github.mtesmct.rieau.api.infra.http.dossiers;

import lombok.Getter;

@Getter
public class JsonMessage {
    private String contenu;
    private String date;
    private JsonUser auteur;

    public JsonMessage(JsonUser auteur, String contenu, String date) {
        this.auteur = auteur;
        this.contenu = contenu;
        this.date = date;
    }
}