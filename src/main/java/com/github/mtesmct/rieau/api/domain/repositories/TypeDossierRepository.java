package com.github.mtesmct.rieau.api.domain.repositories;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;

import java.util.Optional;

public interface TypeDossierRepository {
    public Optional<TypeDossier> findByType(EnumTypes type);
    public Optional<TypeDossier> findByCode(String code);
}
