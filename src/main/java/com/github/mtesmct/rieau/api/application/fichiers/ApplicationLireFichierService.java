package com.github.mtesmct.rieau.api.application.fichiers;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.ApplicationService;
import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.AuthenticationService;
import com.github.mtesmct.rieau.api.application.auth.AuthorizationService;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.DossierNotFoundException;
import com.github.mtesmct.rieau.api.application.dossiers.FichierNotFoundException;
import com.github.mtesmct.rieau.api.application.dossiers.UserNotOwnerException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.MairieForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.FichierService;

@ApplicationService
public class ApplicationLireFichierService implements LireFichierService {

    private FichierService fichierService;
    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;
    private DossierRepository dossierRepository;

    public ApplicationLireFichierService(FichierService fichierService, AuthenticationService authenticationService,
            AuthorizationService authorizationService, DossierRepository dossierRepository) {
        if (authenticationService == null)
            throw new IllegalArgumentException("Le service d'authentification ne peut pas être nul.");
        this.authenticationService = authenticationService;
        if (authorizationService == null)
            throw new IllegalArgumentException("Le service d'authorisation ne peut pas être nul.");
        this.authorizationService = authorizationService;
        if (fichierService == null)
            throw new IllegalArgumentException("Le service des fichiers ne peut pas être nul.");
        this.fichierService = fichierService;
        if (dossierRepository == null)
            throw new IllegalArgumentException("Le repository des dossiers ne peut pas être nul.");
        this.dossierRepository = dossierRepository;
    }

    @Override
    public Optional<Fichier> execute(FichierId fichierId)
            throws FichierNotFoundException, UserForbiddenException, AuthRequiredException, UserInfoServiceException,
            UserNotOwnerException, DossierNotFoundException, MairieForbiddenException {
        this.authorizationService.isDeposantOrMairieAndBetaAuthorized();
        Optional<Fichier> fichier = Optional.empty();
        Personne user = this.authenticationService.user().get();
        Optional<Dossier> dossier = this.dossierRepository.findByFichierId(fichierId.toString());
        if (dossier.isEmpty())
                throw new DossierNotFoundException(fichierId);
        if (this.authenticationService.isDeposant() && !dossier.get().deposant().equals(user)) {
            throw new UserNotOwnerException(user.identity().toString(),
            fichierId.toString());
        }
        if (this.authenticationService.isMairie() && !dossier.get().projet().localisation().adresse().commune().equals(user.adresse().commune())) {
            throw new MairieForbiddenException(user);
        }
        fichier = this.fichierService.findById(fichierId);
        return fichier;
    }

}