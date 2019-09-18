package com.github.mtesmct.rieau.api.infra.uuid;

import java.util.UUID;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierIdService;

import org.springframework.stereotype.Service;

@Service
public class UuidFichierIdService implements FichierIdService {

    @Override
    public FichierId creer() {
        return new FichierId(UUID.randomUUID().toString());
    }
}