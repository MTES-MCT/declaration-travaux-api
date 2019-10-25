package com.github.mtesmct.rieau.api.domain.services;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;

import java.util.Optional;

public interface FichierService {
    public Optional<Fichier> findById(FichierId fichierId) throws FichierServiceException;
    public void save(Fichier fichier) throws FichierServiceException;
    public void delete(FichierId fichierId) throws FichierServiceException;
}
