package com.github.mtesmct.rieau.api.infra.communes;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Commune;
import com.github.mtesmct.rieau.api.domain.services.CommuneService;
import com.github.mtesmct.rieau.api.infra.config.AppProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${app.communes-url:}')")
public class ApiCommuneService implements CommuneService {
    @Autowired
    private AppProperties properties;
    private final RestTemplate restTemplate;

    public ApiCommuneService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Optional<Commune> findByCodeCodePostal(String codePostal) {
        Optional<Commune> commune = Optional.empty();
        JsonCommune[] jsonCommunes = this.restTemplate.getForObject(this.properties.getCommunesUrl(), JsonCommune[].class, codePostal);
        if (jsonCommunes != null && jsonCommunes.length > 0)
            commune = Optional
                    .ofNullable(new Commune(codePostal, jsonCommunes[0].getNom(), jsonCommunes[0].getCodeDepartement()));
        return commune;
    }

}