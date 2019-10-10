package com.github.mtesmct.rieau.api.infra.communes;

import java.time.Duration;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Commune;
import com.github.mtesmct.rieau.api.domain.services.CommuneService;
import com.github.mtesmct.rieau.api.infra.config.AppProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnExpression("!T(org.springframework.util.StringUtils).isEmpty('${app.communes-url:}')")
@Slf4j
public class ApiCommuneService implements CommuneService {

    private AppProperties properties;

    private final RestTemplate restTemplate;

    @Autowired
    public ApiCommuneService(AppProperties properties, RestTemplateBuilder restTemplateBuilder) {
        this.properties = properties;
        long timeout = this.properties.getHttpTimeout().longValue();
        Duration timeoutDuration = Duration.ofMillis(timeout);
        this.restTemplate = restTemplateBuilder.setConnectTimeout(timeoutDuration).setReadTimeout(timeoutDuration).build();
    }

    @Override
    public Optional<Commune> findByCodeCodePostal(String codePostal) {
        Optional<Commune> commune = Optional.empty();
        JsonCommune[] jsonCommunes = new JsonCommune[]{};
        try{
            jsonCommunes = this.restTemplate.getForObject(this.properties.getCommunesUrl(), JsonCommune[].class, codePostal);
        } catch (RestClientException e) {
            log.warn("Impossible de joindre {}. Exception: {}", this.properties.getCommunesUrl(), e.getMessage());
            log.warn("Utilisation du mock commune service");
            commune = Optional.ofNullable(new Commune(codePostal, codePostal, codePostal.substring(0, 2)));
        }
        if (jsonCommunes != null && jsonCommunes.length > 0)
            commune = Optional
                    .ofNullable(new Commune(codePostal, jsonCommunes[0].getNom(), jsonCommunes[0].getCodeDepartement()));
        return commune;
    }

}