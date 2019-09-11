package com.github.mtesmct.rieau.api.infra.uuid;

import java.util.UUID;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierIdService;

import org.springframework.stereotype.Service;

@Service
public class UuidDossierIdService implements DossierIdService {

    @Override
    public DossierId creer() {
        return new DossierId(UUID.randomUUID().toString());
    }
}