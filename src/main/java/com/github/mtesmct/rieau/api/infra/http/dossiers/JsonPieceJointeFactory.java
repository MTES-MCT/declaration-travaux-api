package com.github.mtesmct.rieau.api.infra.http.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;

import org.springframework.stereotype.Component;

@Component
public class JsonPieceJointeFactory {
    
    public JsonPieceJointe toJson(PieceJointe pieceJointe) {
        JsonPieceJointe jsonPieceJointe = null;
        if (pieceJointe != null){
                jsonPieceJointe = new JsonPieceJointe(pieceJointe.code().type().toString(), pieceJointe.code().numero(), pieceJointe.fichierId().toString());
        }
        return jsonPieceJointe;
    }
}