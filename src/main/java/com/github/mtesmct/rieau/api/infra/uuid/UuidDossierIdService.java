package com.github.mtesmct.rieau.api.infra.uuid;

import java.util.UUID;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.services.DossierIdService;

import org.springframework.stereotype.Service;

@Service
public class UuidDossierIdService implements DossierIdService {

    @Override
    public DossierId creer(String typeDossier, String departement, String commune, String annee) {
        return new DossierId(typeDossier + "-" +  departement + "-" + commune + "-" + annee + "-" + UUID.randomUUID().hashCode());
    }
}