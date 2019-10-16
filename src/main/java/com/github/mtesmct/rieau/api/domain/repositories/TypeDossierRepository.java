package com.github.mtesmct.rieau.api.domain.repositories;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;

public interface TypeDossierRepository {
    public Optional<TypeDossier> findByType(EnumTypes type);
    public Optional<TypeDossier> findByCode(String code);
}
