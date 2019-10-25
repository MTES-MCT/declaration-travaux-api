package com.github.mtesmct.rieau.api.domain.repositories;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumStatuts;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatut;

import java.util.List;
import java.util.Optional;

public interface TypeStatutDossierRepository {
    public Optional<TypeStatut> findById(EnumStatuts id);
	public List<TypeStatut> findAllGreaterThan(TypeStatut type);
}
