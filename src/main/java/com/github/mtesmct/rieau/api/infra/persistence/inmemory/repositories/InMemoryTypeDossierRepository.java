package com.github.mtesmct.rieau.api.infra.persistence.inmemory.repositories;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.repositories.TypeDossierRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Optional;

@Component
public class InMemoryTypeDossierRepository implements TypeDossierRepository {
    private EnumMap<EnumTypes, TypeDossier> types;

    @Override
    public Optional<TypeDossier> findByCode(String code) {
        Optional<TypeDossier> type = Optional.empty();
        if (this.types != null)
            type = this.types.values().stream().filter(t -> t.code().equals(code)).findAny();
        return type;
    }

    @Override
    public Optional<TypeDossier> findByType(EnumTypes type) {
        TypeDossier typeDossier = this.types.get(type);
        return Optional.ofNullable(typeDossier);
    }

    @PostConstruct
    public void initTypes() {        
        this.types = new EnumMap<EnumTypes, TypeDossier>(EnumTypes.class);
        this.types.put(EnumTypes.PCMI, new TypeDossier(EnumTypes.PCMI,"13406", Arrays.asList(new String[] { "1", "2", "3", "4", "5", "6", "7", "8" })));
        this.types.put(EnumTypes.DPMI, new TypeDossier(EnumTypes.DPMI,"13703",Arrays.asList(new String[] { "1" })));
    }

}
