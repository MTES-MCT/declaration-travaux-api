package com.github.mtesmct.rieau.api.infra.application.dossiers;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.ApplicationConsulterMonDossierService;
import com.github.mtesmct.rieau.api.application.dossiers.ConsulterMonDossierService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DeposantNonAutoriseException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TxConsulterMonDossierService implements ConsulterMonDossierService {

    @Autowired
    private ApplicationConsulterMonDossierService applicationConsulterMonDossierService;

    @Override
    public Optional<Dossier> execute(String id)
            throws DeposantNonAutoriseException, AuthRequiredException, UserForbiddenException, UserInfoServiceException {
        return this.applicationConsulterMonDossierService.execute(id);
    }    
}