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
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.repositories.PersonnePhysiqueRepository;

@ApplicationService
public class ImporterCerfaService {
    private DossierRepository dossierRepository;
    private PersonnePhysiqueRepository personnePhysiqueRepository;
    private DossierFactory dossierFactory;
    private CerfaImportService cerfaImportService;
    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;

    public ImporterCerfaService(AuthenticationService authenticationService, DossierFactory dossierFactory,
            DossierRepository dossierRepository, PersonnePhysiqueRepository personnePhysiqueRepository,
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
        if (personnePhysiqueRepository == null)
            throw new IllegalArgumentException("Le repository des personnes physiques ne peut pas être nul.");
        this.personnePhysiqueRepository = personnePhysiqueRepository;
        if (cerfaImportService == null)
            throw new IllegalArgumentException("Le service d'import de CERFA ne peut pas être nul.");
        this.cerfaImportService = cerfaImportService;
    }

    public Optional<Dossier> execute(File file) throws DossierImportException {
        this.authorizationService.isDemandeurAndBetaAuthorized();
        PieceJointe cerfa = this.cerfaImportService.lire(this.authenticationService.userId(), file);
        Optional<PersonnePhysique> demandeur = this.personnePhysiqueRepository
                .findByEmail(this.authenticationService.userId().toString());
        if (demandeur.isEmpty())
            throw new DossierImportException(
                    "Aucun demandeur trouvé avec l'email: " + this.authenticationService.userId());
        Dossier dossier = this.dossierFactory.creer(demandeur.get());
        dossier = this.dossierRepository.save(dossier);
        return Optional.ofNullable(dossier);
    }

}