package com.github.mtesmct.rieau.api.infra.http.dossiers;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class JsonDossier {
    private String id;
    private JsonTypeDossier type;
    private List<JsonStatut> statuts = new ArrayList<JsonStatut>();
    private List<JsonStatut> statutsRestants = new ArrayList<JsonStatut>();
    private JsonStatut statutActuel;
    private JsonPieceJointe cerfa;
    private JsonPieceJointe decision;
    private List<JsonPieceJointe> piecesJointes = new ArrayList<JsonPieceJointe>();
    private List<String> piecesAJoindre = new ArrayList<String>();
    private JsonProjet projet;
    private List<JsonMessage> messages = new ArrayList<JsonMessage>();

    public JsonDossier(String id, JsonTypeDossier type, JsonPieceJointe cerfa, JsonProjet projet, JsonStatut statutActuel) {
        this.id = id;
        this.type = type;
        this.cerfa = cerfa;
        this.projet = projet;
        this.statutActuel = statutActuel;
    }
    public void setDecision(JsonPieceJointe decision){
        this.decision = decision;
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

    public void addMessage(JsonMessage message){
        this.messages.add(message);
    }

    public void addStatutRestant(JsonStatut statut){
        this.statutsRestants.add(statut);
    }

}