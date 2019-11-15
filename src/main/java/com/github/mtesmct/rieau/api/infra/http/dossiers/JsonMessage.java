package com.github.mtesmct.rieau.api.infra.http.dossiers;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class JsonMessage {
    private String contenu;
    private LocalDateTime date;
    private JsonUser auteur;

    public JsonMessage(JsonUser auteur, String contenu, LocalDateTime date) {
        this.auteur = auteur;
        this.contenu = contenu;
        this.date = date;
    }
}