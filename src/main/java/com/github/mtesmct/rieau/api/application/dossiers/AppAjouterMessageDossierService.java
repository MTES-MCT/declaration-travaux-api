package com.github.mtesmct.rieau.api.application.dossiers;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.ApplicationService;
import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.AuthenticationService;
import com.github.mtesmct.rieau.api.application.auth.AuthorizationService;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DeposantForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.InstructeurForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Message;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.DateService;

@ApplicationService
public class AppAjouterMessageDossierService implements AjouterMessageDossierService {

    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;
    private DossierRepository dossierRepository;
    private DateService dateService;

    public AppAjouterMessageDossierService(AuthenticationService authenticationService,
            AuthorizationService authorizationService, DossierRepository dossierRepository, DateService dateService) {
        if (authenticationService == null)
            throw new IllegalArgumentException("Le service d'authentification ne peut pas être nul.");
        this.authenticationService = authenticationService;
        if (authorizationService == null)
            throw new IllegalArgumentException("Le service d'autorisation ne peut pas être nul.");
        this.authorizationService = authorizationService;
        if (dossierRepository == null)
            throw new IllegalArgumentException("Le repository des dossiers ne peut pas être nul.");
        this.dossierRepository = dossierRepository;
        if (dateService == null)
            throw new IllegalArgumentException("Le service des dates des dossiers ne peut pas être nul.");
        this.dateService = dateService;
    }

    @Override
    public Optional<Dossier> execute(DossierId id, String contenu) throws DossierNotFoundException,
            InstructeurForbiddenException, AuthRequiredException, UserForbiddenException, UserInfoServiceException,
            TypeStatutNotFoundException, StatutForbiddenException, DeposantForbiddenException {
        this.authorizationService.isDeposantOrInstructeurAndBetaAuthorized();
        Optional<Dossier> dossier = this.dossierRepository.findById(id.toString());
        if (dossier.isEmpty())
            throw new DossierNotFoundException(id);
        Optional<Personne> user = this.authenticationService.user();
        if (user.isEmpty())
            throw new NullPointerException("L'utilisateur connecté ne peut pas être nul");
        if (this.authenticationService.isInstructeur() && user.get().adresse() == null)
            throw new NullPointerException("L'adresse de l'utilisateur connecté ne peut pas être nulle");
        if (this.authenticationService.isInstructeur() && !dossier.isEmpty()
                && !dossier.get().projet().localisation().adresse().commune().equals(user.get().adresse().commune()))
            throw new InstructeurForbiddenException(user.get());
        if (this.authenticationService.isDeposant() && !dossier.isEmpty()
                && !dossier.get().deposant().equals(user.get()))
            throw new DeposantForbiddenException(user.get());
        Message message = new Message(user.get(), this.dateService.now(), contenu);
        dossier.get().ajouterMessage(message);
        Dossier dossierSaved = this.dossierRepository.save(dossier.get());
        dossier = Optional.ofNullable(dossierSaved);
        return dossier;
    }
}