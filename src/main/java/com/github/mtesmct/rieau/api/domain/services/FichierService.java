package com.github.mtesmct.rieau.api.domain.services;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.FichierId;

public interface FichierService {
    public Optional<Fichier> findById(FichierId fichierId) throws FichierServiceException;
    public void save(FichierId fichierId, Fichier fichier) throws FichierServiceException;
    public void delete(FichierId fichierId) throws FichierServiceException;
}
