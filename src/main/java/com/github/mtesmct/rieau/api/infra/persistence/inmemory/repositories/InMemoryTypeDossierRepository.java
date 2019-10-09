package com.github.mtesmct.rieau.api.infra.persistence.inmemory.repositories;

import java.util.EnumMap;
import java.util.Optional;

import javax.annotation.PostConstruct;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.repositories.TypeDossierRepository;

import org.springframework.stereotype.Component;

@Component
public class InMemoryTypeDossierRepository implements TypeDossierRepository {
    private EnumMap<TypesDossier, TypeDossier> types;

    @Override
    public Optional<TypeDossier> findByCode(String code) {
        Optional<TypeDossier> type = Optional.empty();
        if (this.types != null)
            type = this.types.values().stream().filter(t -> t.code().equals(code)).findAny();
        return type;
    }

    @Override
    public Optional<TypeDossier> findByType(TypesDossier type) {
        return Optional.ofNullable(this.types.get(type));
    }

    @PostConstruct
    public void initTypes() {        
        this.types = new EnumMap<>(TypesDossier.class);
        this.types.put(TypesDossier.PCMI, new TypeDossier(TypesDossier.PCMI,"13406"));
        this.types.put(TypesDossier.DP, new TypeDossier(TypesDossier.DP,"13703"));
    }

}
