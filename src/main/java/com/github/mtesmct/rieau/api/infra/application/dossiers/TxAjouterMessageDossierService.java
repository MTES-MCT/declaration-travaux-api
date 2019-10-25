package com.github.mtesmct.rieau.api.infra.application.dossiers;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.AjouterMessageDossierService;
import com.github.mtesmct.rieau.api.application.dossiers.AppAjouterMessageDossierService;
import com.github.mtesmct.rieau.api.application.dossiers.DossierNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class TxAjouterMessageDossierService implements AjouterMessageDossierService {

    @Autowired
    private AppAjouterMessageDossierService appAjouterMessageDossierService;

    @Override
    public Optional<Dossier> execute(DossierId id, String message)
            throws DossierNotFoundException, InstructeurForbiddenException, AuthRequiredException, UserForbiddenException,
            UserInfoServiceException, TypeStatutNotFoundException, StatutForbiddenException, DeposantForbiddenException {
        return this.appAjouterMessageDossierService.execute(id, message);
    }
}