package com.github.mtesmct.rieau.api.application.dossiers;

import com.github.mtesmct.rieau.api.application.ApplicationService;
import com.github.mtesmct.rieau.api.application.auth.*;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DeposantForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.InstructeurForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.MairieForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.FichierService;

import java.util.Optional;

@ApplicationService
public class AppSupprimerDossierService implements SupprimerDossierService {

    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;
    private DossierRepository dossierRepository;
    private FichierService fichierService;

    public AppSupprimerDossierService(AuthenticationService authenticationService,
            AuthorizationService authorizationService, DossierRepository dossierRepository, FichierService fichierService) {
        if (authenticationService == null)
            throw new IllegalArgumentException("Le service d'authentification ne peut pas être nul.");
        this.authenticationService = authenticationService;
        if (authorizationService == null)
            throw new IllegalArgumentException("Le service d'autorisation ne peut pas être nul.");
        this.authorizationService = authorizationService;
        if (dossierRepository == null)
            throw new IllegalArgumentException("Le repository des dossiers ne peut pas être nul.");
        this.dossierRepository = dossierRepository;
        if (fichierService == null)
            throw new IllegalArgumentException("Le service des fichiers ne peut pas être nul.");
        this.fichierService = fichierService;
    }

    @Override
    public void execute(String id) throws DeposantForbiddenException, AuthRequiredException, UserForbiddenException,
            UserInfoServiceException, MairieForbiddenException, InstructeurForbiddenException,
            DossierNotFoundException {
        this.authorizationService.isDeposantAndBetaAuthorized();
        Optional<Dossier> dossier = this.dossierRepository.findById(id);
        Optional<Personne> user = this.authenticationService.user();
        if (user.isEmpty())
            throw new NullPointerException("L'utilisateur connecté ne peut pas être nul");
        if (!dossier.isEmpty() && dossier.get().deposant() == null)
            throw new NullPointerException("Le déposant du dossier ne peut pas être nul");
        if (this.authenticationService.isDeposant() && !dossier.isEmpty()
                && !dossier.get().deposant().equals(user.get()))
            throw new DeposantForbiddenException(user.get());
        this.dossierRepository.delete(dossier.get().identity());
        dossier.get().pieceJointes().forEach(pj -> this.fichierService.delete(pj.fichierId()));
    }
}