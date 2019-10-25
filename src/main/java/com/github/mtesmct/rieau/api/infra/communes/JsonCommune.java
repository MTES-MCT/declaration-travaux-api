package com.github.mtesmct.rieau.api.infra.communes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonCommune {
    private String nom;
    private String code;
    private String codeDepartement;
    private String codeRegion;

    public JsonCommune(String nom, String code, String codeDepartement, String codeRegion) {
        this.nom = nom;
        this.code = code;
        this.codeDepartement = codeDepartement;
        this.codeRegion = codeRegion;
    }

    public JsonCommune() {
    }
}   