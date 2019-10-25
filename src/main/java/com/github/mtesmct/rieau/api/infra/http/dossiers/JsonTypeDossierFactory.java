package com.github.mtesmct.rieau.api.infra.http.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import org.springframework.stereotype.Component;

@Component
public class JsonTypeDossierFactory {

    public JsonTypeDossier toJson(TypeDossier type) {
        JsonTypeDossier jsonType = null;
        if (type != null) {
            jsonType = new JsonTypeDossier(type.type().toString(), type.type().libelle());
        }
        return jsonType;
    }
}