package com.github.mtesmct.rieau.api.application.dossiers;

import com.github.mtesmct.rieau.api.application.ApplicationService;
import com.github.mtesmct.rieau.api.application.auth.*;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.*;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.StatutService;

import java.util.Optional;

@ApplicationService
public class AppDeclarerCompletDossierService implements DeclarerCompletDossierService {

    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;
    private DossierRepository dossierRepository;
    private StatutService statutService;

    public AppDeclarerCompletDossierService(AuthenticationService authenticationService,
            AuthorizationService authorizationService, DossierRepository dossierRepository,
            StatutService statutService) {
        if (authenticationService == null)
            throw new IllegalArgumentException("Le service d'authentification ne peut pas être nul.");
        this.authenticationService = authenticationService;
        if (authorizationService == null)
            throw new IllegalArgumentException("Le service d'autorisation ne peut pas être nul.");
        this.authorizationService = authorizationService;
        if (dossierRepository == null)
            throw new IllegalArgumentException("Le repository des dossiers ne peut pas être nul.");
        this.dossierRepository = dossierRepository;
        if (statutService == null)
            throw new IllegalArgumentException("Le service des statuts des dossiers ne peut pas être nul.");
        this.statutService = statutService;
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
        this.statutService.declarerComplet(dossier.get());
        Dossier dossierComplet = this.dossierRepository.save(dossier.get());
        dossier = Optional.ofNullable(dossierComplet);
        return dossier;
    }
}