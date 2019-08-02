package com.github.mtesmct.rieau.api.depositaire.infra.http;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot;
import com.github.mtesmct.rieau.api.depositaire.infra.date.DateConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DepotWebAdapter {
    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter dateTimeConverter;
    public JsonDepot toJson(Depot depot){
        return JsonDepot.builder().id(depot.getId()).type(depot.getType()).etat(depot.getEtat()).date(this.dateTimeConverter.format(depot.getDate())).build();
    }
}