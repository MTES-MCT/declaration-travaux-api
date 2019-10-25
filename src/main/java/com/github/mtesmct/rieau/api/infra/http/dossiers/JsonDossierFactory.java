package com.github.mtesmct.rieau.api.infra.http.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.*;
import com.github.mtesmct.rieau.api.domain.services.StatutService;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class JsonDossierFactory {
    @Autowired
    private DateConverter<LocalDateTime> localDateTimeConverter;
    @Autowired
    private JsonPieceJointeFactory jsonPieceJointeFactory;
    @Autowired
    private JsonProjetFactory jsonProjetFactory;
    @Autowired
    private JsonStatutFactory jsonStatutFactory;
    @Autowired
    private JsonMessageFactory jsonMessageFactory;
    @Autowired
    private JsonTypeDossierFactory jsonTypeDossierFactory;
    @Autowired
    private StatutService statutService;

    public JsonDossier toJson(Dossier dossier) {
        if (dossier == null)
            throw new NullPointerException("Le dossier ne peut pas être nul.");
        JsonDossier jsonDossier = new JsonDossier(Objects.toString(dossier.identity()),
                this.jsonTypeDossierFactory.toJson(dossier.type()),
                this.jsonPieceJointeFactory.toJson(dossier.cerfa()), this.jsonProjetFactory.toJson(dossier.projet()),
                this.jsonStatutFactory.toJson(dossier.statutActuel().get()));
        dossier.pieceJointes().forEach(pieceJointe -> this.ajouterPieceJointe(jsonDossier, pieceJointe));
        dossier.piecesAJoindre().forEach(numero -> this.ajouterPieceAJoindre(jsonDossier, numero));
        dossier.historiqueStatuts().forEach((statut) -> this.ajouterStatut(jsonDossier, statut));
        dossier.messages().forEach((message) -> this.ajouterMessage(jsonDossier, message));
        if (dossier.decision() != null) jsonDossier.setDecision(this.jsonPieceJointeFactory.toJson(dossier.decision()));
        this.statutService.statutsRestants(dossier).forEach(type -> this.ajouterStatutRestant(jsonDossier, type));

        return jsonDossier;
    }

    private void ajouterPieceJointe(JsonDossier jsonDossier, PieceJointe pieceJointe) {
        jsonDossier.addPieceJointe(this.jsonPieceJointeFactory.toJson(pieceJointe));
    }

    private void ajouterPieceAJoindre(JsonDossier jsonDossier, String numero) {
        jsonDossier.addPieceJointe(numero);
    }

    private void ajouterStatut(JsonDossier jsonDossier, Statut statut) {
        jsonDossier.addStatut(this.jsonStatutFactory.toJson(statut));
    }

    private void ajouterStatutRestant(JsonDossier jsonDossier, TypeStatut type) {
        jsonDossier.addStatutRestant(this.jsonStatutFactory.toJson(type));
    }

    private void ajouterMessage(JsonDossier jsonDossier, Message message) {
        jsonDossier.addMessage(this.jsonMessageFactory.toJson(message));
    }
}