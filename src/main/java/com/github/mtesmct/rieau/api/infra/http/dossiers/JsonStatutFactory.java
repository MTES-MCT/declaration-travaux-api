package com.github.mtesmct.rieau.api.infra.http.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Statut;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class JsonStatutFactory {
    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter dateTimeConverter;

    public JsonStatut toJson(Statut statut) {
        JsonStatut jsonStatut = null;
        if (statut != null) {
            jsonStatut = new JsonStatut(statut.type().statut().toString(), statut.type().statut().libelle(), statut.type().joursDelais(), this.dateTimeConverter.format(statut.dateDebut()));
        }
        return jsonStatut;
    }
}