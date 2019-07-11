package com.github.mtesmct.rieau.api.depositaire.infra.http;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Demande;
import com.github.mtesmct.rieau.api.depositaire.infra.date.DateConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DemandeWebAdapter {
    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter dateTimeConverter;
    public JsonDemande toJson(Demande demande){
        return JsonDemande.builder().id(demande.getId()).type(demande.getType()).etat(demande.getEtat()).date(this.dateTimeConverter.format(demande.getDate())).build();
    }
}