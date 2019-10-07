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
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.repositories.TypeDossierRepository;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.domain.services.FichierServiceException;

import lombok.extern.slf4j.Slf4j;

@ApplicationService
@Slf4j
public class ApplicationImporterCerfaService implements ImporterCerfaService {
    private DossierRepository dossierRepository;
    private DossierFactory dossierFactory;
    private CerfaImportService cerfaImportService;
    private TypeDossierRepository typeDossierRepository;
    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;
    private FichierFactory fichierFactory;
    private FichierService fichierService;
    private ProjetFactory projetFactory;

    public ApplicationImporterCerfaService(AuthenticationService authenticationService,
            AuthorizationService authorizationService, DossierFactory dossierFactory,
            DossierRepository dossierRepository, CerfaImportService cerfaImportService,
            TypeDossierRepository typeDossierRepository, FichierFactory fichierFactory, FichierService fichierService,
            ProjetFactory projetFactory) {
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
        if (projetFactory == null)
            throw new IllegalArgumentException("La factory des projets ne peut pas être nulle.");
        this.projetFactory = projetFactory;
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
                throw new DossierImportException(new FichierNotFoundException(fichier.identity().toString()));
            Optional<String> code = this.cerfaImportService.lireCode(fichierLu.get());
            if (code.isEmpty())
                throw new DossierImportException(new CodeCerfaNotFoundException());
            Optional<TypeDossier> type = this.typeDossierRepository.findByCode(code.get());
            if (type.isEmpty())
                throw new DossierImportException(
                        "Aucun type de dossier reconnu dans le fichier pdf pour le code {" + code.get() + "}");
            Personne deposant = this.authenticationService.user().get();
            Optional<String> codePostal = this.cerfaImportService.lireFormValue(fichierLu.get(), type.get().type(),
                    "codePostal");
            if (codePostal.isEmpty())
                throw new DossierImportException("Le code postal ne peut pas être vide",
                        new FormValueCerfaNotFoundException("codePostal"));
            Optional<String> numeroVoie = this.cerfaImportService.lireFormValue(fichierLu.get(), type.get().type(),
                    "numeroVoie");
            if (numeroVoie.isEmpty())
                log.debug("", new DossierImportException(new FormValueCerfaNotFoundException("numeroVoie")));
            Optional<String> voie = this.cerfaImportService.lireFormValue(fichierLu.get(), type.get().type(), "voie");
            if (voie.isEmpty())
                log.debug("", new DossierImportException(new FormValueCerfaNotFoundException("voie")));
            Optional<String> lieuDit = this.cerfaImportService.lireFormValue(fichierLu.get(), type.get().type(),
                    "lieuDit");
            if (lieuDit.isEmpty())
                log.debug("", new DossierImportException(new FormValueCerfaNotFoundException("lieuDit")));
            Optional<String> bp = this.cerfaImportService.lireFormValue(fichierLu.get(), type.get().type(), "bp");
            if (bp.isEmpty())
                log.debug("", new DossierImportException(new FormValueCerfaNotFoundException("bp")));
            Optional<String> cedex = this.cerfaImportService.lireFormValue(fichierLu.get(), type.get().type(), "cedex");
            if (cedex.isEmpty())
                log.debug("", new DossierImportException(new FormValueCerfaNotFoundException("cedex")));
            Optional<String> nouvelleConstruction = this.cerfaImportService.lireFormValue(fichierLu.get(),
                    type.get().type(), "nouvelleConstruction");
            if (nouvelleConstruction.isEmpty())
                log.debug("", new DossierImportException(new FormValueCerfaNotFoundException("nouvelleConstruction")));
            Optional<String> prefixe = this.cerfaImportService.lireFormValue(fichierLu.get(), type.get().type(),
                    "prefixe");
            if (prefixe.isEmpty())
                log.debug("", new DossierImportException(new FormValueCerfaNotFoundException("prefixe")));
            Optional<String> section = this.cerfaImportService.lireFormValue(fichierLu.get(), type.get().type(),
                    "section");
            if (section.isEmpty())
                log.debug("", new DossierImportException(new FormValueCerfaNotFoundException("section")));
            Optional<String> numeroCadastre = this.cerfaImportService.lireFormValue(fichierLu.get(), type.get().type(),
                    "numeroCadastre");
            if (numeroCadastre.isEmpty())
                log.debug("", new DossierImportException(new FormValueCerfaNotFoundException("numeroCadastre")));

            Optional<Projet> projet = Optional.empty();
            try {
                projet = Optional.ofNullable(
                        this.projetFactory.creer(numeroVoie.get(), voie.get(), lieuDit.get(), codePostal.get(),
                                bp.get(), cedex.get(), new ParcelleCadastrale(prefixe.get(), section.get(), numeroCadastre.get()),
                                Boolean.parseBoolean(nouvelleConstruction.get())));
            } catch (CommuneNotFoundException e) {
                throw new DossierImportException(e);
            }
            if (projet.isEmpty())
                throw new DossierImportException(new ProjetNotFoundException());
            dossier = this.dossierFactory.creer(deposant, type.get().type(), projet.get());
            dossier.ajouterCerfa(fichier.identity());
            dossier = this.dossierRepository.save(dossier);
            fichierLu.get().fermer();
        } catch (FichierServiceException | CerfaImportException | IOException | NullPointerException e) {
            throw new DossierImportException(e);
        }
        return Optional.ofNullable(dossier);
    }

}