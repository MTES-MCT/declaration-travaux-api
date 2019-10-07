package com.github.mtesmct.rieau.api.domain.factories;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.Factory;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.repositories.TypeDossierRepository;
import com.github.mtesmct.rieau.api.domain.services.DateService;
import com.github.mtesmct.rieau.api.domain.services.DossierIdService;

@Factory
public class DossierFactory {
    private DossierIdService dossierIdService;
    private TypeDossierRepository typeDossierRepository;
    private DateService dateService;

    public DossierFactory(DossierIdService dossierIdService, DateService dateService,
            TypeDossierRepository typeDossierRepository) {
        if (dossierIdService == null)
            throw new NullPointerException("Le service des id des dossiers ne peut pas être nul.");
        this.dossierIdService = dossierIdService;
        if (dateService == null)
            throw new NullPointerException("Le service des dates de depot des dossiers ne peut pas être nul.");
        this.dateService = dateService;
        if (typeDossierRepository == null)
            throw new NullPointerException("Le repository des types de dossier ne peut pas être nul.");
        this.typeDossierRepository = typeDossierRepository;
    }

    public Dossier creer(Personne deposant, TypesDossier type, Projet projet) {
        if (deposant == null)
            throw new NullPointerException("Le deposant du dossier ne peut pas être nul.");
        if (type == null)
            throw new NullPointerException("Le type de dossier ne peut pas être nul.");
        Optional<TypeDossier> typeDossier = this.typeDossierRepository.findByType(type);
        if (typeDossier.isEmpty())
            throw new IllegalArgumentException("Aucun type de dossier correspondant à type=" + type.toString());
        if (projet == null)
            throw new NullPointerException("Le projet du dossier ne peut pas être nul.");
        return new Dossier(this.dossierIdService.creer(type.toString(), projet.localisation().adresse().commune().department(), projet.localisation().adresse().commune().codePostal(), this.dateService.year()), deposant, StatutDossier.DEPOSE,
                this.dateService.now(), typeDossier.orElseThrow());
    }
}