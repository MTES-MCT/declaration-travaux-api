package com.github.mtesmct.rieau.api.infra.application.dossiers;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.ApplicationQualifierDossierService;
import com.github.mtesmct.rieau.api.application.dossiers.DossierNotFoundException;
import com.github.mtesmct.rieau.api.application.dossiers.QualifierDossierService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.MairieForbiddenException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TxQualifierDossierService implements QualifierDossierService {

    @Autowired
    private ApplicationQualifierDossierService applicationQualifierDossierService;

    @Override
    public Optional<Dossier> execute(DossierId id) throws DossierNotFoundException, MairieForbiddenException,
            AuthRequiredException, UserForbiddenException, UserInfoServiceException {
        return this.applicationQualifierDossierService.execute(id);
    }
}