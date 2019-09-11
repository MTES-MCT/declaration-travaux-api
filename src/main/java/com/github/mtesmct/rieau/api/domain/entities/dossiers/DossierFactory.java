package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;

public class DossierFactory {
    private DossierIdService dossierIdService;

    public DossierFactory(DossierIdService dossierIdService) {
        this.dossierIdService = dossierIdService;
    }

    public Dossier creer(PersonnePhysique demandeur) {
        if (dossierIdService == null)
            throw new NullPointerException("Le service des id de dossier ne peut pas être nul.");
        return new Dossier(this.dossierIdService.creer(), demandeur);
    }
}