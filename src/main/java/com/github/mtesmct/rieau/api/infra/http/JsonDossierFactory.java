package com.github.mtesmct.rieau.api.infra.http;

import java.util.Objects;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class JsonDossierFactory {
    @Autowired
    @Qualifier("dateTimeConverter")
    private DateConverter dateTimeConverter;
    
    public Optional<JsonDossier> toJson(Optional<Dossier> optionaldossier){
		Optional<JsonDossier> jsonDossier = Optional.empty();
        if (optionaldossier.isPresent()) {
            Dossier dossier = optionaldossier.get();
            jsonDossier = Optional.ofNullable(new JsonDossier(Objects.toString(dossier.identity()), Objects.toString(dossier.type()), Objects.toString(dossier.statut()), this.dateTimeConverter.format(dossier.dateDepot())));
        }
        return jsonDossier;
    }
}