package com.github.mtesmct.rieau.api.application.dossiers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.ApplicationService;
import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.AuthenticationService;
import com.github.mtesmct.rieau.api.application.auth.AuthorizationService;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
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
    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;
    private FichierFactory fichierFactory;
    private FichierService fichierService;
    private ProjetFactory projetFactory;

    public ApplicationImporterCerfaService(AuthenticationService authenticationService,
            AuthorizationService authorizationService, DossierFactory dossierFactory,
            DossierRepository dossierRepository, CerfaImportService cerfaImportService, FichierFactory fichierFactory,
            FichierService fichierService, ProjetFactory projetFactory) {
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
            this.authorizationService.isDeposantOrMairieAndBetaAuthorized();
            Fichier fichier = this.fichierFactory.creer(is, nom, mimeType, taille);
            this.fichierService.save(fichier);
            Optional<Fichier> fichierLu = this.fichierService.findById(fichier.identity());
            if (fichierLu.isEmpty())
                throw new DossierImportException(new FichierNotFoundException(fichier.identity().toString()));
            Map<String, String> valeurs = this.cerfaImportService.lire(fichierLu.get());
            String type = valeurs.get("type");
            if (type == null)
                throw new DossierImportException("Aucun type de dossier reconnu dans le fichier pdf pour le code");
            Personne deposant = this.authenticationService.user().get();
            for (String nomChamp : this.cerfaImportService.keys(TypesDossier.valueOf(type))) {
                if (Objects.equals(nomChamp, "codePostal") && Objects.equals(valeurs.get(nomChamp), "null"))
                    throw new DossierImportException("Le champ code postal ne peut pas être vide",
                            new FormValueCerfaNotFoundException(nomChamp));
                if (Objects.equals(valeurs.get(nomChamp), "null"))
                    log.debug("{}", new FormValueCerfaNotFoundException(nomChamp).getMessage());
            }

            Optional<Projet> projet = Optional.empty();
            try {
                projet = Optional.ofNullable(this.projetFactory.creer(valeurs.get("numeroVoie"), valeurs.get("voie"),
                        valeurs.get("lieuDit"), valeurs.get("codePostal"), valeurs.get("bp"), valeurs.get("cedex"),
                        new ParcelleCadastrale(valeurs.get("prefixe"), valeurs.get("section"),
                                valeurs.get("numeroCadastre")),
                                Boolean.parseBoolean(valeurs.get("nouvelleConstruction")), Boolean.parseBoolean(valeurs.get("lotissement"))));
            } catch (CommuneNotFoundException e) {
                throw new DossierImportException(e);
            }
            if (projet.isEmpty())
                throw new DossierImportException(new ProjetNotFoundException());
            dossier = this.dossierFactory.creer(deposant, TypesDossier.valueOf(type), projet.get());
            dossier.ajouterCerfa(fichier.identity());
            dossier = this.dossierRepository.save(dossier);
            fichierLu.get().fermer();
        } catch (FichierServiceException | CerfaImportException | IOException | NullPointerException e) {
            throw new DossierImportException(e);
        }
        return Optional.ofNullable(dossier);
    }

}