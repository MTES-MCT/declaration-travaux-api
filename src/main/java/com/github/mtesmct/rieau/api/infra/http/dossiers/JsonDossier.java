package com.github.mtesmct.rieau.api.infra.http.dossiers;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class JsonDossier {
    private String id;
    private String type;
    private List<JsonStatut> statuts = new ArrayList<JsonStatut>();
    private JsonStatut statutActuel;
    private JsonPieceJointe cerfa;
    private List<JsonPieceJointe> piecesJointes = new ArrayList<JsonPieceJointe>();
    private List<String> piecesAJoindre = new ArrayList<String>();
    private JsonProjet projet;

    public JsonDossier(String id, String type, JsonPieceJointe cerfa, JsonProjet projet, JsonStatut statutActuel) {
        this.id = id;
        this.type = type;
        this.cerfa = cerfa;
        this.projet = projet;
        this.statutActuel = statutActuel;
    }

    public void addPieceJointe(JsonPieceJointe pieceJointe){
        this.piecesJointes.add(pieceJointe);
    }

    public void addPieceJointe(String numero){
        this.piecesAJoindre.add(numero);
    }

    public void addStatut(JsonStatut statut){
        this.statuts.add(statut);
    }

}