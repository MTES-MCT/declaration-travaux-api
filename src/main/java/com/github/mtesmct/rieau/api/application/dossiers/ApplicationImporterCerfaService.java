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
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
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

    public ApplicationImporterCerfaService(AuthenticationService authenticationService,
            AuthorizationService authorizationService, DossierFactory dossierFactory,
            DossierRepository dossierRepository, CerfaImportService cerfaImportService, CerfaService cerfaService) {
        if (authenticationService == null)
            throw new IllegalArgumentException("Le service d'authentification ne peut pas être nul.");
        this.authenticationService = authenticationService;
        if (authorizationService == null)
            throw new IllegalArgumentException("Le service d'authorisation ne peut pas être nul.");
        this.authorizationService = authorizationService;
        if (dossierFactory == null)
            throw new IllegalArgumentException("La factory de dossiers ne peut pas être nulle.");
        this.dossierFactory = dossierFactory;
        if (dossierRepository == null)
            throw new IllegalArgumentException("Le repository de dossier ne peut pas être nul.");
        this.dossierRepository = dossierRepository;
        if (cerfaImportService == null)
            throw new IllegalArgumentException("Le service d'import de CERFA ne peut pas être nul.");
        this.cerfaImportService = cerfaImportService;
        if (cerfaService == null)
            throw new IllegalArgumentException("Le service de CERFA ne peut pas être nul.");
        this.cerfaService = cerfaService;
    }

    @Override
    public Optional<Dossier> execute(File file) throws DossierImportException {
        this.authorizationService.isDeposantAndBetaAuthorized();
        Optional<String> code = this.cerfaImportService.lireCode(file);
        if (code.isEmpty())
            throw new DossierImportException("Pas de code CERFA reconnu dans le texte du pdf");
        PieceJointe cerfa = new PieceJointe(new CodePieceJointe(TypePieceJointe.CERFA, null), file);
        Optional<TypeDossier> type = this.cerfaService.fromCodeCerfa(code.get());
        if (type.isEmpty())
            throw new DossierImportException("Type de dossier indéterminé");
        Personne deposant = this.authenticationService.user().get();
        Dossier dossier = this.dossierFactory.creer(deposant, cerfa, type.get());
        dossier = this.dossierRepository.save(dossier);
        return Optional.ofNullable(dossier);
    }

}