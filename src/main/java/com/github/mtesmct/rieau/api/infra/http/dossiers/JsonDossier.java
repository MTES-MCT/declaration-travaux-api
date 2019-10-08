package com.github.mtesmct.rieau.api.infra.http.dossiers;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class JsonDossier {
    private String id;
    private String type;
    private String statut;
    private String date;
    private JsonPieceJointe cerfa;
    private List<JsonPieceJointe> piecesJointes = new ArrayList<JsonPieceJointe>();
    private List<String> piecesAJoindre = new ArrayList<String>();
    private JsonProjet projet;

    public JsonDossier(String id, String type, String statut, String date, JsonPieceJointe cerfa, JsonProjet projet) {
        this.id = id;
        this.type = type;
        this.statut = statut;
        this.date = date;
        this.cerfa = cerfa;
        this.projet = projet;
    }

    public void addPieceJointe(JsonPieceJointe pieceJointe){
        this.piecesJointes.add(pieceJointe);
    }

    public void addPieceJointe(String numero){
        this.piecesAJoindre.add(numero);
    }

}