package com.github.mtesmct.rieau.api.infra.application.dossiers;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.auth.AuthRequiredException;
import com.github.mtesmct.rieau.api.application.auth.UserForbiddenException;
import com.github.mtesmct.rieau.api.application.auth.UserInfoServiceException;
import com.github.mtesmct.rieau.api.application.dossiers.ApplicationConsulterDossierService;
import com.github.mtesmct.rieau.api.application.dossiers.ConsulterDossierService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DeposantForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.MairieForbiddenException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TxConsulterDossierService implements ConsulterDossierService {

    @Autowired
    private ApplicationConsulterDossierService applicationConsulterDossierService;

    @Override
    public Optional<Dossier> execute(String id) throws DeposantForbiddenException, AuthRequiredException,
            UserForbiddenException, UserInfoServiceException, MairieForbiddenException {
        return this.applicationConsulterDossierService.execute(id);
    }    
}