package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.Factory;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;
import com.github.mtesmct.rieau.api.domain.services.DateService;
@Factory
public class DossierFactory {
    private DossierIdService dossierIdService;
    private DateService dateService;

    public DossierFactory(DossierIdService dossierIdService, DateService dateService) {
        this.dossierIdService = dossierIdService;
        this.dateService = dateService;
    }

    public Dossier creer(PersonnePhysique demandeur) {
        if (dossierIdService == null)
            throw new NullPointerException("Le service des id des dossiers ne peut pas être nul.");
            if (dateService == null)
                throw new NullPointerException("Le service des dates de depot des dossiers ne peut pas être nul.");
        return new Dossier(this.dossierIdService.creer(), demandeur, this.dateService.now());
    }
}