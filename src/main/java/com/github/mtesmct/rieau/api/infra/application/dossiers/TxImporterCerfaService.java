package com.github.mtesmct.rieau.api.infra.application.dossiers;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.ApplicationImporterCerfaService;
import com.github.mtesmct.rieau.api.application.dossiers.DossierImportException;
import com.github.mtesmct.rieau.api.application.dossiers.ImporterCerfaService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TxImporterCerfaService implements ImporterCerfaService {

    @Autowired
    private ApplicationImporterCerfaService applicationImporterCerfaService;

    @Override
    public Optional<Dossier> execute(Fichier fichier)
            throws DossierImportException, AuthRequiredException, UserForbiddenException, UserServiceException {
        return this.applicationImporterCerfaService.execute(fichier);
    }


    
}