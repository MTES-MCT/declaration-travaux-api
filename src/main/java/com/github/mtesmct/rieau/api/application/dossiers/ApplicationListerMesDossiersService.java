package com.github.mtesmct.rieau.api.application.dossiers;

import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.ApplicationService;
import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.AuthenticationService;
import com.github.mtesmct.rieau.api.application.auth.AuthorizationService;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;

@ApplicationService
public class ApplicationListerMesDossiersService implements ListerMesDossiersService {

    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;
    private DossierRepository dossierRepository;

    public ApplicationListerMesDossiersService(AuthenticationService authenticationService,
            AuthorizationService authorizationService, DossierRepository dossierRepository) {
        if (authenticationService == null)
            throw new IllegalArgumentException("Le service d'authentification ne peut pas être nul.");
        this.authenticationService = authenticationService;
        if (authorizationService == null)
            throw new IllegalArgumentException("Le service d'autorisation ne peut pas être nul.");
        this.authorizationService = authorizationService;
        if (dossierRepository == null)
            throw new IllegalArgumentException("Le repository de dossier ne peut pas être nul.");
        this.dossierRepository = dossierRepository;
    }

    @Override
    public List<Dossier> execute() throws AuthRequiredException, UserForbiddenException, UserInfoServiceException {
        this.authorizationService.isDeposantAndBetaAuthorized();
        Optional<Personne> user = this.authenticationService.user();
        if (user.isEmpty())
            throw new IllegalArgumentException("L'utilisateur connecté est vide");
        return this.dossierRepository.findByDeposantId(this.authenticationService.user().get().identity().toString());
    }
}