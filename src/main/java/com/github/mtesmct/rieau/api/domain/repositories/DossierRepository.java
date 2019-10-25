package com.github.mtesmct.rieau.api.domain.repositories;

import com.github.mtesmct.rieau.api.application.dossiers.DossierNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierId;

import java.util.List;
import java.util.Optional;

public interface DossierRepository {
    public List<Dossier> findByDeposantId(String deposantId);
    public List<Dossier> findByCommune(String commune);
    public Dossier save(Dossier dossier);
    public Optional<Dossier> findById(String id);
    public void delete(DossierId id) throws DossierNotFoundException;
	public Optional<Dossier> findByFichierId(String fichierId);
}