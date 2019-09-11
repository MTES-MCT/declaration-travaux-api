package com.github.mtesmct.rieau.api.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;

public interface DossierRepository {
    public List<Dossier> findByDemandeur(String demandeurId);
    public Dossier save(Dossier dossier);
	public Optional<Dossier> findByDemandeurAndId(String demandeurId, String id);
}