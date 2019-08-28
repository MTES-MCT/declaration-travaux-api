package com.github.mtesmct.rieau.api.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.Depot;

public interface DepotRepository {
    public List<Depot> findByDepositaire(String depositaire);
    public Depot save(Depot depot);
	public Optional<Depot> findByDepositaireAndId(String depositaire, String id);
}