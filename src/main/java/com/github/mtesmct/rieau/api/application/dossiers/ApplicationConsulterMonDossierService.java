package com.github.mtesmct.rieau.api.application.dossiers;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.ApplicationService;
import com.github.mtesmct.rieau.api.application.auth.AuthenticationService;
import com.github.mtesmct.rieau.api.application.auth.AuthorizationService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;

@ApplicationService
public class ApplicationConsulterMonDossierService implements ConsulterMonDossierService {

    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;
    private DossierRepository dossierRepository;

    private void validate(AuthenticationService authenticationService, AuthorizationService authorizationService,
            DossierRepository dossierRepository) {
        if (authenticationService == null)
            throw new IllegalArgumentException("Le service d'authentification ne peut pas être nul.");
        if (authorizationService == null)
            throw new IllegalArgumentException("Le service d'autorisation ne peut pas être nul.");
        if (dossierRepository == null)
            throw new IllegalArgumentException("Le repository des dossiers ne peut pas être nul.");
    }

    public ApplicationConsulterMonDossierService(AuthenticationService authenticationService,
            AuthorizationService authorizationService, DossierRepository dossierRepository) {
        this.validate(authenticationService, authorizationService, dossierRepository);
        this.authenticationService = authenticationService;
        this.authorizationService = authorizationService;
        this.dossierRepository = dossierRepository;
    }

    @Override
    public Optional<Dossier> execute(String id) {
        this.validate(this.authenticationService, this.authorizationService, this.dossierRepository);
        this.authorizationService.isDeposantAndBetaAuthorized();
        return this.dossierRepository
                .findByDeposantIdAndId(this.authenticationService.user().get().identity().toString(), id);
    }
}