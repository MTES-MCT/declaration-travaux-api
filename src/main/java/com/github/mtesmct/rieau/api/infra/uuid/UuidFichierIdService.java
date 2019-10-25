package com.github.mtesmct.rieau.api.infra.uuid;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.services.FichierIdService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UuidFichierIdService implements FichierIdService {

    @Override
    public FichierId creer() {
        return new FichierId(UUID.randomUUID().toString());
    }
}