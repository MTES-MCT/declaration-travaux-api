package com.github.mtesmct.rieau.api.application.dossiers;

import java.io.File;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.ApplicationService;
import com.github.mtesmct.rieau.api.application.auth.AuthenticationService;
import com.github.mtesmct.rieau.api.application.auth.AuthorizationService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierFactory;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysiqueId;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;

@ApplicationService
public class ImporterCerfaService {
    private DossierRepository dossierRepository;
    private DossierFactory dossierFactory;
    private CerfaImportService cerfaImportService;
    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;

    public ImporterCerfaService(AuthenticationService authenticationService, DossierFactory dossierFactory,
            DossierRepository dossierRepository,
            CerfaImportService cerfaImportService) {
        if (authenticationService == null)
            throw new IllegalArgumentException("Le service d'authentification ne peut pas être nul.");
        this.authenticationService = authenticationService;
        if (dossierFactory == null)
            throw new IllegalArgumentException("La factory de dossiers ne peut pas être nulle.");
        this.dossierFactory = dossierFactory;
        if (dossierRepository == null)
            throw new IllegalArgumentException("Le repository de dossier ne peut pas être nul.");
        this.dossierRepository = dossierRepository;
        if (cerfaImportService == null)
            throw new IllegalArgumentException("Le service d'import de CERFA ne peut pas être nul.");
        this.cerfaImportService = cerfaImportService;
    }

    public Optional<Dossier> execute(File file) throws DossierImportException {
        this.authorizationService.isDeposantAndBetaAuthorized();
        PieceJointe cerfa = this.cerfaImportService.lire(this.authenticationService.userId(), file);
        PersonnePhysique deposant = new PersonnePhysique(new PersonnePhysiqueId(this.authenticationService.userId().toString()), null, null, null, null, null);
        Dossier dossier = this.dossierFactory.creer(deposant, cerfa);
        dossier = this.dossierRepository.save(dossier);
        return Optional.ofNullable(dossier);
    }

}