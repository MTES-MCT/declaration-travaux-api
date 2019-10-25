package com.github.mtesmct.rieau.api.infra.communes;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Commune;
import com.github.mtesmct.rieau.api.domain.services.CommuneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@ConditionalOnExpression("T(org.springframework.util.StringUtils).isEmpty('${app.communes-url:}')")
@Slf4j
public class MockCommuneService implements CommuneService {

    @Override
    public Optional<Commune> findByCodeCodePostal(String codePostal) {
        log.debug("Utilisation du mock communes service");
        return Optional.ofNullable(new Commune(codePostal, codePostal, codePostal.substring(0, 2)));
    }

}