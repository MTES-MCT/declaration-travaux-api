package com.github.mtesmct.rieau.api.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;

public interface DossierRepository {
    public List<Dossier> findByDeposant(String deposantId);
    public Dossier save(Dossier dossier);
	public Optional<Dossier> findByDeposantAndId(String deposantId, String id);
}