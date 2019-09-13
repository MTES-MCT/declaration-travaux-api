package com.github.mtesmct.rieau.api.application.dossiers;

import java.io.File;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.ApplicationService;
import com.github.mtesmct.rieau.api.application.auth.AuthenticationService;
import com.github.mtesmct.rieau.api.application.auth.AuthorizationService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.CodePieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierFactory;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypePieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.CerfaService;

@ApplicationService
public class ApplicationImporterCerfaService implements ImporterCerfaService {
    private DossierRepository dossierRepository;
    private DossierFactory dossierFactory;
    private CerfaImportService cerfaImportService;
    private CerfaService cerfaService;
    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;

    private void validate(AuthenticationService authenticationService, AuthorizationService authorizationService,
            DossierFactory dossierFactory, DossierRepository dossierRepository, CerfaImportService cerfaImportService,
            CerfaService cerfaService) {
        if (authenticationService == null)
            throw new IllegalArgumentException("Le service d'authentification ne peut pas être nul.");
        if (authorizationService == null)
            throw new IllegalArgumentException("Le service d'authorisation ne peut pas être nul.");
        if (dossierFactory == null)
            throw new IllegalArgumentException("La factory de dossiers ne peut pas être nulle.");
        if (dossierRepository == null)
            throw new IllegalArgumentException("Le repository de dossier ne peut pas être nul.");
        if (cerfaImportService == null)
            throw new IllegalArgumentException("Le service d'import de CERFA ne peut pas être nul.");
        if (cerfaService == null)
            throw new IllegalArgumentException("Le service de CERFA ne peut pas être nul.");

    }

    public ApplicationImporterCerfaService(AuthenticationService authenticationService,
            AuthorizationService authorizationService, DossierFactory dossierFactory,
            DossierRepository dossierRepository, CerfaImportService cerfaImportService, CerfaService cerfaService) {
        this.validate(authenticationService, authorizationService, dossierFactory, dossierRepository,
                cerfaImportService, cerfaService);
        this.authenticationService = authenticationService;
        this.authorizationService = authorizationService;
        this.dossierFactory = dossierFactory;
        this.dossierRepository = dossierRepository;
        this.cerfaImportService = cerfaImportService;
        this.cerfaService = cerfaService;
    }

    @Override
    public Optional<Dossier> execute(File file) throws DossierImportException {
        this.validate(this.authenticationService, this.authorizationService, this.dossierFactory,
                this.dossierRepository, this.cerfaImportService, this.cerfaService);
        this.authorizationService.isDeposantAndBetaAuthorized();
        Optional<String> code = this.cerfaImportService.lireCode(file);
        if (code.isEmpty())
            throw new DossierImportException("Pas de code CERFA reconnu dans le texte du pdf");
        PieceJointe cerfa = new PieceJointe(new CodePieceJointe(TypePieceJointe.CERFA, null), file);
        Optional<TypeDossier> type = this.cerfaService.fromCodeCerfa(code.get());
        if (type.isEmpty())
            throw new DossierImportException("Type de dossier indéterminé");
        PersonnePhysique deposant = this.authenticationService.user().get();
        Dossier dossier = this.dossierFactory.creer(deposant, cerfa, type.get());
        dossier = this.dossierRepository.save(dossier);
        return Optional.ofNullable(dossier);
    }

}