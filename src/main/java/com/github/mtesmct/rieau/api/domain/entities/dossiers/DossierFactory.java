package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.Factory;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;
import com.github.mtesmct.rieau.api.domain.services.DateService;

@Factory
public class DossierFactory {
    private DossierIdService dossierIdService;
    private DateService dateService;

    private void validate(DossierIdService dossierIdService, DateService dateService) {
        if (dossierIdService == null)
            throw new NullPointerException("Le service des id des dossiers ne peut pas être nul.");
        if (dateService == null)
            throw new NullPointerException("Le service des dates de depot des dossiers ne peut pas être nul.");
    }

    public DossierFactory(DossierIdService dossierIdService, DateService dateService) {
        this.validate(dossierIdService, dateService);
        this.dossierIdService = dossierIdService;
        this.dateService = dateService;
    }

    public Dossier creer(PersonnePhysique deposant, PieceJointe cerfa, TypeDossier type) {
        this.validate(this.dossierIdService, this.dateService);
        return new Dossier(this.dossierIdService.creer(), deposant, this.dateService.now(), cerfa, type);
    }
}