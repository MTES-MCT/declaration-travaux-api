package com.github.mtesmct.rieau.api.infra.application.dossiers;

import java.io.InputStream;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.ApplicationImporterCerfaService;
import com.github.mtesmct.rieau.api.application.dossiers.DossierImportException;
import com.github.mtesmct.rieau.api.application.dossiers.ImporterCerfaService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TxImporterCerfaService implements ImporterCerfaService {

    @Autowired
    private ApplicationImporterCerfaService applicationImporterCerfaService;

    @Override
    public Optional<Dossier> execute(InputStream is, String nom, String mimeType, long taille)
            throws DossierImportException, AuthRequiredException, UserForbiddenException, UserInfoServiceException {
        return this.applicationImporterCerfaService.execute(is, nom, mimeType, taille);
    }


    
}