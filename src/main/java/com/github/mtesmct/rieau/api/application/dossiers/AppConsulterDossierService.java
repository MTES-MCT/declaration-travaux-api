package com.github.mtesmct.rieau.api.application.dossiers;

import com.github.mtesmct.rieau.api.application.ApplicationService;
import com.github.mtesmct.rieau.api.application.auth.*;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DeposantForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.InstructeurForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.MairieForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;

import java.util.Optional;

@ApplicationService
public class AppConsulterDossierService implements ConsulterDossierService {

    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;
    private DossierRepository dossierRepository;

    public AppConsulterDossierService(AuthenticationService authenticationService,
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
    public Optional<Dossier> execute(String id) throws DeposantForbiddenException, AuthRequiredException,
            UserForbiddenException, UserInfoServiceException, MairieForbiddenException, InstructeurForbiddenException {
        this.authorizationService.isUserAuthorized();
        Optional<Dossier> dossier = this.dossierRepository.findById(id);
        Optional<Personne> user = this.authenticationService.user();
        if (user.isEmpty())
            throw new NullPointerException("L'utilisateur connecté ne peut pas être nul");
        if (!dossier.isEmpty() && dossier.get().deposant() == null)
            throw new NullPointerException("Le déposant du dossier ne peut pas être nul");
        if (this.authenticationService.isDeposant() && !dossier.isEmpty()
                && !dossier.get().deposant().equals(user.get()))
            throw new DeposantForbiddenException(user.get());
        if ((this.authenticationService.isMairie() || this.authenticationService.isInstructeur())
                && user.get().adresse() == null)
            throw new NullPointerException("L'adresse de l'utilisateur connecté ne peut pas être nulle");
        if (this.authenticationService.isMairie() && !dossier.isEmpty()
                && !dossier.get().projet().localisation().adresse().commune().equals(user.get().adresse().commune()))
            throw new MairieForbiddenException(user.get());
        if (this.authenticationService.isInstructeur() && !dossier.isEmpty()
                && !dossier.get().projet().localisation().adresse().commune().equals(user.get().adresse().commune()))
            throw new InstructeurForbiddenException(user.get());
        return dossier;
    }
}