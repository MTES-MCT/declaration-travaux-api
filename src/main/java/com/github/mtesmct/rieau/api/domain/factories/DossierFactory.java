package com.github.mtesmct.rieau.api.domain.factories;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.Factory;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossierNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.User;
import com.github.mtesmct.rieau.api.domain.repositories.TypeDossierRepository;
import com.github.mtesmct.rieau.api.domain.services.DossierIdService;
import com.github.mtesmct.rieau.api.domain.services.StatutService;

@Factory
public class DossierFactory {
    private DossierIdService dossierIdService;
    private TypeDossierRepository typeDossierRepository;
    private StatutService statutService;

    public DossierFactory(DossierIdService dossierIdService, 
            TypeDossierRepository typeDossierRepository, StatutService statutService) {
        if (dossierIdService == null)
            throw new NullPointerException("Le service des id des dossiers ne peut pas être nul.");
        this.dossierIdService = dossierIdService;
        if (typeDossierRepository == null)
            throw new NullPointerException("Le repository des types de dossier ne peut pas être nul.");
        this.typeDossierRepository = typeDossierRepository;
        if (statutService == null)
            throw new NullPointerException("Le service des statuts de dossier des dossiers ne peut pas être nul.");
        this.statutService = statutService;
    }

    public Dossier creer(User deposant, EnumTypes type, Projet projet, FichierId fichierIdCerfa)
            throws StatutForbiddenException, TypeStatutNotFoundException, TypeDossierNotFoundException {
        if (deposant == null)
            throw new NullPointerException("Le déposant du dossier ne peut pas être nul.");
        if (type == null)
            throw new NullPointerException("Le type de dossier ne peut pas être nul.");
        Optional<TypeDossier> typeDossier = this.typeDossierRepository.findByType(type);
        if (typeDossier.isEmpty())
            throw new TypeDossierNotFoundException(type);
        if (projet == null)
            throw new NullPointerException("Le projet du dossier ne peut pas être nul.");
        Dossier dossier = new Dossier(
                this.dossierIdService.creer(type.toString(), projet.localisation().adresse().commune()), deposant,
                typeDossier.get(), projet, fichierIdCerfa);
        this.statutService.deposer(dossier);
        return dossier;
    }
}