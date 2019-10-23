package com.github.mtesmct.rieau.api.infra.http.dossiers;

import lombok.Getter;

@Getter
public class JsonStatut {
    private String id;
    private String libelle;
    private Integer joursDelai;
    private String dateDebut;
    private Integer ordre;

    public JsonStatut(String id, Integer ordre, String libelle, Integer joursDelai, String dateDebut) {
        this.id = id;
        this.ordre = ordre;
        this.libelle = libelle;
        this.joursDelai = joursDelai;
        this.dateDebut = dateDebut;
    }
}