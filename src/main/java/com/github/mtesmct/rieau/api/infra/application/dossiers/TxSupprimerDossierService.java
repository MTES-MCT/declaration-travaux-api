package com.github.mtesmct.rieau.api.infra.application.dossiers;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.AppSupprimerDossierService;
import com.github.mtesmct.rieau.api.application.dossiers.DossierNotFoundException;
import com.github.mtesmct.rieau.api.application.dossiers.SupprimerDossierService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DeposantForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.InstructeurForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.MairieForbiddenException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TxSupprimerDossierService implements SupprimerDossierService {

    @Autowired
    private AppSupprimerDossierService appSupprimerDossierService;

    @Override
    public void execute(String id)
            throws DeposantForbiddenException, AuthRequiredException, UserForbiddenException, UserInfoServiceException,
            MairieForbiddenException, InstructeurForbiddenException, DossierNotFoundException {
        this.appSupprimerDossierService.execute(id);
    }
}