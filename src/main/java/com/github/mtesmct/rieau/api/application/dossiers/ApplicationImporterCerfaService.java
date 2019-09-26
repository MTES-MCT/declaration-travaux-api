package com.github.mtesmct.rieau.api.application.dossiers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.ApplicationService;
import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.AuthenticationService;
import com.github.mtesmct.rieau.api.application.auth.AuthorizationService;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.repositories.TypeDossierRepository;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.domain.services.FichierServiceException;

@ApplicationService
public class ApplicationImporterCerfaService implements ImporterCerfaService {
    private DossierRepository dossierRepository;
    private DossierFactory dossierFactory;
    private CerfaImportService cerfaImportService;
    private TypeDossierRepository typeDossierRepository;
    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;
    private FichierFactory fichierFactory;
    private FichierService fichierService;

    public ApplicationImporterCerfaService(AuthenticationService authenticationService,
            AuthorizationService authorizationService, DossierFactory dossierFactory,
            DossierRepository dossierRepository, CerfaImportService cerfaImportService,
            TypeDossierRepository typeDossierRepository, FichierFactory fichierFactory, FichierService fichierService) {
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
        if (typeDossierRepository == null)
            throw new IllegalArgumentException("Le repository des types de dossier ne peut pas être nul.");
        this.typeDossierRepository = typeDossierRepository;
        if (fichierService == null)
            throw new IllegalArgumentException("Le service des fichiers ne peut pas être nul.");
        this.fichierService = fichierService;
        if (fichierFactory == null)
            throw new IllegalArgumentException("La factory des fichiers ne peut pas être nulle.");
        this.fichierFactory = fichierFactory;
    }

    @Override
    public Optional<Dossier> execute(InputStream is, String nom, String mimeType, long taille)
            throws DossierImportException, AuthRequiredException, UserForbiddenException, UserInfoServiceException {
        Dossier dossier;
        try {
            this.authorizationService.isDeposantAndBetaAuthorized();
            Fichier fichier = this.fichierFactory.creer(is, nom, mimeType, taille);
            this.fichierService.save(fichier);
            Optional<Fichier> fichierLu = this.fichierService.findById(fichier.identity());
            if (fichierLu.isEmpty())
                throw new DossierImportException("Le fichier n'a pas pu être lu depuis le magasin de fichier");
            Optional<String> code = this.cerfaImportService.lireCode(fichierLu.get());
            if (code.isEmpty())
                throw new DossierImportException("Code CERFA non reconnu dans le fichier pdf");
            Optional<TypeDossier> type = this.typeDossierRepository.findByCode(code.get());
            if (type.isEmpty())
                throw new DossierImportException("Type de dossier non reconnu dans le fichier pdf");
            Personne deposant = this.authenticationService.user().get();
            dossier = this.dossierFactory.creer(deposant, type.get().type());
            dossier.ajouterCerfa(fichier.identity());
            dossier = this.dossierRepository.save(dossier);
            fichierLu.get().fermer();
        } catch (FichierServiceException | CerfaImportException | IOException e) {
            throw new DossierImportException(e);
        }
        return Optional.ofNullable(dossier);
    }

}