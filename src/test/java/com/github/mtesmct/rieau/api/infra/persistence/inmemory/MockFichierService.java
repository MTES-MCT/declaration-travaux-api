package com.github.mtesmct.rieau.api.infra.persistence.inmemory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.domain.services.FichierServiceException;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;

@TestComponent
@Primary
public class MockFichierService implements FichierService {

    Map<FichierId, Fichier> fichiers = new HashMap<FichierId, Fichier>();

    @Override
    public Optional<Fichier> findById(FichierId fichierId) throws FichierServiceException {
        return Optional.ofNullable(this.fichiers.get(fichierId));
    }

    @Override
    public void save(FichierId fichierId, Fichier fichier) throws FichierServiceException {
        this.fichiers.put(fichierId, fichier);
    }

    @Override
    public void delete(FichierId fichierId) throws FichierServiceException {
        this.fichiers.remove(fichierId);
    }
    
}