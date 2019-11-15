package com.github.mtesmct.rieau.api.infra.http.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Statut;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatut;
import com.github.mtesmct.rieau.api.domain.services.DateService;
import com.github.mtesmct.rieau.api.domain.services.StatutService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JsonStatutFactory {
    @Autowired
    private DateService dateService;
    @Autowired
    private StatutService statutService;

    public JsonStatut toJson(Statut statut) {
        JsonStatut jsonStatut = null;
        if (statut != null) {
            jsonStatut = new JsonStatut(statut.type().identity().toString(), statut.type().ordre(),
                    statut.type().libelle(), statut.type().joursDelais(), this.statutService.joursRestants(statut),
                    statut.dateDebut());
        }
        return jsonStatut;
    }

    public JsonStatut toJson(TypeStatut type) {
        JsonStatut jsonStatut = null;
        if (type != null) {
            jsonStatut = new JsonStatut(type.identity().toString(), type.ordre(), type.libelle(), type.joursDelais(),
                    type.joursDelais(), this.dateService.now());
        }
        return jsonStatut;
    }
}