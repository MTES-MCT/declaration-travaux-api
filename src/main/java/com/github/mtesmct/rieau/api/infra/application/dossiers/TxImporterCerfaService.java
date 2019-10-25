package com.github.mtesmct.rieau.api.infra.application.dossiers;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.AppImporterCerfaService;
import com.github.mtesmct.rieau.api.application.dossiers.DossierImportException;
import com.github.mtesmct.rieau.api.application.dossiers.ImporterCerfaService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.Optional;

@Service
@Transactional
public class TxImporterCerfaService implements ImporterCerfaService {

    @Autowired
    private AppImporterCerfaService applicationImporterCerfaService;

    @Override
    public Optional<Dossier> execute(InputStream is, String nom, String mimeType, long taille)
            throws DossierImportException, AuthRequiredException, UserForbiddenException, UserInfoServiceException,
            StatutForbiddenException, TypeStatutNotFoundException, PieceNonAJoindreException, TypeDossierNotFoundException {
        return this.applicationImporterCerfaService.execute(is, nom, mimeType, taille);
    }


    
}