package com.github.mtesmct.rieau.api.infra.http.dossiers;

import lombok.Getter;

@Getter
public class JsonUser {
    private String id;
    private String nom;
    private String prenom;
    private String[] profils;

    public JsonUser(String id, String nom, String prenom, String[] profils) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.profils = profils;
    }
}