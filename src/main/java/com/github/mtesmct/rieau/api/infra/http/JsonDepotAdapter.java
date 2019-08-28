package com.github.mtesmct.rieau.api.infra.http;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.Depot;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class JsonDepotAdapter {
    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter dateTimeConverter;
    public Optional<JsonDepot> toJson(Optional<Depot> optionaldepot){
		Optional<JsonDepot> jsonDepot = Optional.empty();
        if (optionaldepot.isPresent()) {
            Depot depot = optionaldepot.get();
            jsonDepot = Optional.ofNullable(JsonDepot.builder().id(depot.getId()).type(depot.getType().toString()).etat(depot.getEtat().toString()).date(this.dateTimeConverter.format(depot.getDate())).build());
        }
        return jsonDepot;
    }
}