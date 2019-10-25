package com.github.mtesmct.rieau.api.infra.http.dossiers;

import lombok.Getter;

@Getter
public class JsonStatut {
    private String id;
    private String libelle;
    private Integer joursDelai;
    private Integer joursRestants;
    private String dateDebut;
    private Integer ordre;

    public JsonStatut(String id, Integer ordre, String libelle, Integer joursDelai, Integer joursRestants, String dateDebut) {
        this.id = id;
        this.ordre = ordre;
        this.libelle = libelle;
        this.joursDelai = joursDelai;
        this.joursRestants = joursRestants;
        this.dateDebut = dateDebut;
    }
}