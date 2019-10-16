package com.github.mtesmct.rieau.api.domain.factories;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.Factory;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumStatuts;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossierNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatut;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.repositories.TypeDossierRepository;
import com.github.mtesmct.rieau.api.domain.repositories.TypeStatutDossierRepository;
import com.github.mtesmct.rieau.api.domain.services.DateService;
import com.github.mtesmct.rieau.api.domain.services.DossierIdService;

@Factory
public class DossierFactory {
    private DossierIdService dossierIdService;
    private TypeDossierRepository typeDossierRepository;
    private TypeStatutDossierRepository statutDossierRepository;
    private DateService dateService;

    public DossierFactory(DossierIdService dossierIdService, DateService dateService,
            TypeDossierRepository typeDossierRepository, TypeStatutDossierRepository statutDossierRepository) {
        if (dossierIdService == null)
            throw new NullPointerException("Le service des id des dossiers ne peut pas être nul.");
        this.dossierIdService = dossierIdService;
        if (dateService == null)
            throw new NullPointerException("Le service des dates de depot des dossiers ne peut pas être nul.");
        this.dateService = dateService;
        if (typeDossierRepository == null)
            throw new NullPointerException("Le repository des types de dossier ne peut pas être nul.");
        this.typeDossierRepository = typeDossierRepository;
        if (statutDossierRepository == null)
            throw new NullPointerException("Le repository des statuts de dossier ne peut pas être nul.");
        this.statutDossierRepository = statutDossierRepository;
    }

    public Dossier creer(Personne deposant, EnumTypes type, Projet projet, FichierId fichierIdCerfa)
            throws StatutForbiddenException, TypeStatutNotFoundException, TypeDossierNotFoundException {
        if (deposant == null)
            throw new NullPointerException("Le deposant du dossier ne peut pas être nul.");
        if (type == null)
            throw new NullPointerException("Le type de dossier ne peut pas être nul.");
        Optional<TypeDossier> typeDossier = this.typeDossierRepository.findByType(type);
        if (typeDossier.isEmpty())
            throw new TypeDossierNotFoundException(type);
        if (projet == null)
            throw new NullPointerException("Le projet du dossier ne peut pas être nul.");
        Optional<TypeStatut> typeStatut = this.statutDossierRepository.findByStatut(EnumStatuts.DEPOSE);
        if (typeStatut.isEmpty())
            throw new TypeStatutNotFoundException(EnumStatuts.DEPOSE);
        Dossier dossier = new Dossier(
                this.dossierIdService.creer(type.toString(), projet.localisation().adresse().commune()), deposant,
                typeDossier.get(), projet, fichierIdCerfa);
        dossier.ajouterStatut(this.dateService.now(), typeStatut.get());
        return dossier;
    }
}