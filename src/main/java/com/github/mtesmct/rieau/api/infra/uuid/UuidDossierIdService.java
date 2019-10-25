package com.github.mtesmct.rieau.api.infra.uuid;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Commune;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.services.DateService;
import com.github.mtesmct.rieau.api.domain.services.DossierIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UuidDossierIdService implements DossierIdService {
    @Autowired
    private DateService dateService;

    @Override
    public DossierId creer(String typeDossier, Commune commune) {
        return new DossierId(typeDossier + "-" +  commune.department() + "-" + commune.codePostal() + "-" + this.dateService.year() + "-" + UUID.randomUUID().hashCode());
    }
}