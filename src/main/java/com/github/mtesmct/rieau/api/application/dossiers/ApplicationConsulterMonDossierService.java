package com.github.mtesmct.rieau.api.application.dossiers;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.ApplicationService;
import com.github.mtesmct.rieau.api.application.auth.AuthenticationService;
import com.github.mtesmct.rieau.api.application.auth.AuthorizationService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DeposantNonAutoriseException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;

@ApplicationService
public class ApplicationConsulterMonDossierService implements ConsulterMonDossierService {

    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;
    private DossierRepository dossierRepository;

    public ApplicationConsulterMonDossierService(AuthenticationService authenticationService,
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
    public Optional<Dossier> execute(String id) throws DeposantNonAutoriseException {
        this.authorizationService.isDeposantAndBetaAuthorized();
        Optional<Dossier> dossier = this.dossierRepository.findById(id);
        if (!dossier.isEmpty() && dossier.get().deposant() == null)
            throw new NullPointerException("Le déposant du dossier ne peut pas être nul");
        Optional<Personne> user = this.authenticationService.user();
        if (user.isEmpty())
            throw new NullPointerException("L'utilisateur connecté ne peut pas être nul");
        if (!dossier.isEmpty() && !dossier.get().deposant().equals(user.get()))
            throw new DeposantNonAutoriseException(user.get());
        return dossier;
    }
}