package com.github.mtesmct.rieau.api.infra.http.dossiers;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class JsonStatut {
    private String id;
    private String libelle;
    private Integer joursDelai;
    private Integer joursRestants;
    private LocalDateTime dateDebut;
    private Integer ordre;

    public JsonStatut(String id, Integer ordre, String libelle, Integer joursDelai, Integer joursRestants, LocalDateTime dateDebut) {
        this.id = id;
        this.ordre = ordre;
        this.libelle = libelle;
        this.joursDelai = joursDelai;
        this.joursRestants = joursRestants;
        this.dateDebut = dateDebut;
    }
}