package com.github.mtesmct.rieau.api.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumStatuts;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatut;

public interface TypeStatutDossierRepository {
    public Optional<TypeStatut> findById(EnumStatuts id);
	public List<TypeStatut> findAllGreaterThan(TypeStatut type);
}
