package com.github.mtesmct.rieau.api.infra.persistence.inmemory.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.domain.services.FichierServiceException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnExpression("'${minio.enabled},${app.fichiers-dir}'=='false,'")
public class InMemoryFichierService implements FichierService {

    private Map<String, Fichier> fichiers;

    @PostConstruct
    public void init(){
        this.fichiers = new HashMap<String, Fichier>();
    }

    @Override
    public Optional<Fichier> findById(FichierId fichierId) throws FichierServiceException {
        return Optional.ofNullable(this.fichiers.get(fichierId.toString()));
    }

    @Override
    public void save(Fichier fichier) throws FichierServiceException {
        this.fichiers.put(fichier.identity().toString(), fichier);
    }

    @Override
    public void delete(FichierId fichierId) throws FichierServiceException {
        this.fichiers.remove(fichierId.toString());
    }

}