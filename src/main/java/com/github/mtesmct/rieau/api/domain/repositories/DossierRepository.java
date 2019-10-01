package com.github.mtesmct.rieau.api.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;

public interface DossierRepository {
    public List<Dossier> findByDeposantId(String deposantId);
    public Dossier save(Dossier dossier);
    public Optional<Dossier> findById(String id);
    public boolean isDeposantOwner(String deposantId, String fichierId);
}