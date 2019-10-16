package com.github.mtesmct.rieau.api.infra.http.dossiers;

import lombok.Getter;

@Getter
public class JsonStatut {
    private String id;
    private String libelle;
    private Integer joursDelai;
    private String dateDebut;

    public JsonStatut(String id, String libelle, Integer joursDelai, String dateDebut) {
        this.id = id;
        this.libelle = libelle;
        this.joursDelai = joursDelai;
        this.dateDebut = dateDebut;
    }
}