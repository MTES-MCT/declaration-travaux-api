package com.github.mtesmct.rieau.api.infra.http.dossiers;

import lombok.Getter;

@Getter
public class JsonAdresse {
    private String codePostal;
    private String commune;
    private String numero;
    private String voie;
    private String lieuDit;
    private String bp;
    private String cedex;
    private String[] parcelles;

    public JsonAdresse(String codePostal, String commune, String numero, String voie, String lieuDit, String bp,
            String cedex, String[] parcelles) {
        this.codePostal = codePostal;
        this.commune = commune;
        this.numero = numero;
        this.voie = voie;
        this.lieuDit = lieuDit;
        this.bp = bp;
        this.cedex = cedex;
        this.parcelles = parcelles;
    }

    
}