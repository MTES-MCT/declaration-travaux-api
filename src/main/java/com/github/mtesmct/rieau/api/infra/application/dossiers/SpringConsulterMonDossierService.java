package com.github.mtesmct.rieau.api.infra.application.dossiers;

import java.util.Optional;

import com.github.mtesmct.rieau.api.application.dossiers.ApplicationConsulterMonDossierService;
import com.github.mtesmct.rieau.api.application.dossiers.ConsulterMonDossierService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SpringConsulterMonDossierService implements ConsulterMonDossierService {

    @Autowired
    private ApplicationConsulterMonDossierService applicationConsulterMonDossierService;

    @Override
    public Optional<Dossier> execute(String id) {
        return this.applicationConsulterMonDossierService.execute(id);
    }    
}