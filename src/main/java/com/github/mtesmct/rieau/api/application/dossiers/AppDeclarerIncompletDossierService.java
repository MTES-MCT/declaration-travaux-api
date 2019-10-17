package com.github.mtesmct.rieau.api.application.dossiers;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.ApplicationService;
import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.AuthenticationService;
import com.github.mtesmct.rieau.api.application.auth.AuthorizationService;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumStatuts;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.InstructeurForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatut;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.repositories.TypeStatutDossierRepository;
import com.github.mtesmct.rieau.api.domain.services.DateService;

@ApplicationService
public class AppDeclarerIncompletDossierService implements DeclarerIncompletDossierService {

    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;
    private DossierRepository dossierRepository;
    private TypeStatutDossierRepository statutDossierRepository;
    private DateService dateService;

    public AppDeclarerIncompletDossierService(AuthenticationService authenticationService,
            AuthorizationService authorizationService, DossierRepository dossierRepository,
            TypeStatutDossierRepository statutDossierRepository, DateService dateService) {
        if (authenticationService == null)
            throw new IllegalArgumentException("Le service d'authentification ne peut pas être nul.");
        this.authenticationService = authenticationService;
        if (authorizationService == null)
            throw new IllegalArgumentException("Le service d'autorisation ne peut pas être nul.");
        this.authorizationService = authorizationService;
        if (dossierRepository == null)
            throw new IllegalArgumentException("Le repository des dossiers ne peut pas être nul.");
        this.dossierRepository = dossierRepository;
        if (statutDossierRepository == null)
            throw new IllegalArgumentException("Le repository des statuts des dossiers ne peut pas être nul.");
        this.statutDossierRepository = statutDossierRepository;
        if (dateService == null)
            throw new IllegalArgumentException("Le service des dates ne peut pas être nul.");
        this.dateService = dateService;
    }

    @Override
    public Optional<Dossier> execute(DossierId id)
            throws DossierNotFoundException, InstructeurForbiddenException, AuthRequiredException, UserForbiddenException,
            UserInfoServiceException, TypeStatutNotFoundException, StatutForbiddenException {
        this.authorizationService.isInstructeurAuthorized();
        Optional<Dossier> dossier = this.dossierRepository.findById(id.toString());
        if (dossier.isEmpty())
            throw new DossierNotFoundException(id);
        Optional<Personne> user = this.authenticationService.user();
        if (user.isEmpty())
            throw new NullPointerException("L'utilisateur connecté ne peut pas être nul");
        if (this.authenticationService.isInstructeur() && !dossier.isEmpty()
                && !dossier.get().projet().localisation().adresse().commune().equals(user.get().adresse().commune()))
            throw new InstructeurForbiddenException(user.get());
        Optional<TypeStatut> typeStatut = this.statutDossierRepository.findByStatut(EnumStatuts.INCOMPLET);
        if (typeStatut.isEmpty())
            throw new TypeStatutNotFoundException(EnumStatuts.INCOMPLET);
        dossier.get().ajouterStatut(this.dateService.now(), typeStatut.get());
        Dossier dossierIncomplet = this.dossierRepository.save(dossier.get());
        dossier = Optional.ofNullable(dossierIncomplet);
        return dossier;
    }
}