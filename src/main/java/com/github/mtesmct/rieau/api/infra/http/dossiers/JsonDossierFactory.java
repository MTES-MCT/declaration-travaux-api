package com.github.mtesmct.rieau.api.infra.http.dossiers;

import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class JsonDossierFactory {
    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter dateTimeConverter;
    @Autowired
    private JsonPieceJointeFactory jsonPieceJointeFactory;
    @Autowired
    private JsonProjetFactory jsonProjetFactory;
    
    public JsonDossier toJson(Dossier dossier){
        if (dossier == null)
            throw new NullPointerException("Le dossier ne peut pas être nul.");
        JsonDossier jsonDossier = new JsonDossier(Objects.toString(dossier.identity()), Objects.toString(dossier.type() != null ? dossier.type().type() : "null"), Objects.toString(dossier.statut()), this.dateTimeConverter.format(dossier.dateDepot()), this.jsonPieceJointeFactory.toJson(dossier.cerfa()), this.jsonProjetFactory.toJson(dossier.projet()));
        dossier.pieceJointes().forEach(pieceJointe -> this.ajouterPieceJointe(jsonDossier, pieceJointe));
        dossier.piecesAJoindre().forEach(numero -> this.ajouterPieceAJoindre(jsonDossier, numero));

        return jsonDossier;
    }

    private void ajouterPieceJointe(JsonDossier jsonDossier, PieceJointe pieceJointe){
        jsonDossier.addPieceJointe(this.jsonPieceJointeFactory.toJson(pieceJointe));
    }


    private void ajouterPieceAJoindre(JsonDossier jsonDossier, String numero){
        jsonDossier.addPieceJointe(numero);
    }
}