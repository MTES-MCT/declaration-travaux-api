package com.github.mtesmct.rieau.api.infra.http.dossiers;

import lombok.Getter;

@Getter
public class JsonPieceJointe {
    private String type;
    private String numero;
    private String fichierId;

    public JsonPieceJointe(String type, String numero, String fichierId) {
        this.type = type;
        this.numero = numero;
        this.fichierId = fichierId;
    }

}