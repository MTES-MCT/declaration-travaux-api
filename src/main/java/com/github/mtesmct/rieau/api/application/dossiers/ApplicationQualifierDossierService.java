package com.github.mtesmct.rieau.api.application.dossiers;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.ApplicationService;
import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.AuthenticationService;
import com.github.mtesmct.rieau.api.application.auth.AuthorizationService;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.MairieForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;

@ApplicationService
public class ApplicationQualifierDossierService implements QualifierDossierService {

    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;
    private DossierRepository dossierRepository;

    public ApplicationQualifierDossierService(AuthenticationService authenticationService,
            AuthorizationService authorizationService, DossierRepository dossierRepository) {
        if (authenticationService == null)
            throw new IllegalArgumentException("Le service d'authentification ne peut pas être nul.");
        this.authenticationService = authenticationService;
        if (authorizationService == null)
            throw new IllegalArgumentException("Le service d'autorisation ne peut pas être nul.");
        this.authorizationService = authorizationService;
        if (dossierRepository == null)
            throw new IllegalArgumentException("Le repository des dossiers ne peut pas être nul.");
        this.dossierRepository = dossierRepository;
    }

    @Override
    public Optional<Dossier> execute(String id) throws DossierNotFoundException, MairieForbiddenException,
            AuthRequiredException, UserForbiddenException, UserInfoServiceException {
        this.authorizationService.isMairieAndBetaAuthorized();
        Optional<Dossier> dossier = this.dossierRepository.findById(id);
        if (dossier.isEmpty())
            throw new DossierNotFoundException(id);
        Optional<Personne> user = this.authenticationService.user();
        if (user.isEmpty())
            throw new NullPointerException("L'utilisateur connecté ne peut pas être nul");
        if (this.authenticationService.isMairie() && !dossier.isEmpty()
                && !dossier.get().projet().localisation().adresse().commune().equals(user.get().adresse().commune()))
            throw new MairieForbiddenException(user.get());
        dossier.get().qualifier();
        Dossier dossierQualifie = this.dossierRepository.save(dossier.get());
        dossier = Optional.ofNullable(dossierQualifie);
        return dossier;
    }
}