package com.github.mtesmct.rieau.api.application.dossiers;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.ApplicationService;
import com.github.mtesmct.rieau.api.application.auth.AuthenticationService;
import com.github.mtesmct.rieau.api.application.auth.AuthorizationService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.AjouterPieceJointeException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DeposantNonAutoriseException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierIdService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.NumeroPieceJointeException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.FichierService;

@ApplicationService
public class ApplicationAJouterPieceJointeService implements AjouterPieceJointeService {
    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;
    private DossierRepository dossierRepository;
    private FichierService fichierService;
    private FichierIdService fichierIdService;

    public ApplicationAJouterPieceJointeService(AuthenticationService authenticationService,
            AuthorizationService authorizationService, DossierRepository dossierRepository,
            FichierIdService fichierIdService, FichierService fichierService) {
        if (authenticationService == null)
            throw new IllegalArgumentException("Le service d'authentification ne peut pas être nul.");
        this.authenticationService = authenticationService;
        if (authorizationService == null)
            throw new IllegalArgumentException("Le service d'authorisation ne peut pas être nul.");
        this.authorizationService = authorizationService;
        if (dossierRepository == null)
            throw new IllegalArgumentException("Le repository des dossiers ne peut pas être nul.");
        this.dossierRepository = dossierRepository;
        if (fichierIdService == null)
            throw new IllegalArgumentException("Le service d'id des fichiers ne peut pas être nul.");
        this.fichierIdService = fichierIdService;
        if (fichierService == null)
            throw new IllegalArgumentException("Le service des fichiers ne peut pas être nul.");
        this.fichierService = fichierService;
    }

    @Override
    public Optional<PieceJointe> execute(Dossier dossier, String numero, Fichier fichier)
            throws AjouterPieceJointeException {
        this.authorizationService.isDeposantAndBetaAuthorized();
        if (numero.equals("0"))
            throw new AjouterPieceJointeException(new NumeroPieceJointeException());
        if (!dossier.deposant().identity().equals(this.authenticationService.user().get().identity()))
            throw new AjouterPieceJointeException(
                    new DeposantNonAutoriseException(this.authenticationService.user().get()));
        FichierId fichierId = this.fichierIdService.creer();
        this.fichierService.save(fichierId, fichier);
        Optional<PieceJointe> pieceJointe = dossier.ajouter(numero, fichierId);
        this.dossierRepository.save(dossier);
        return pieceJointe;
    }

}