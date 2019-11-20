package com.github.mtesmct.rieau.api.infra.persistence.inmemory.repositories;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumStatuts;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatut;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutComparator;
import com.github.mtesmct.rieau.api.domain.repositories.TypeStatutDossierRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InMemoryStatutDossierRepository implements TypeStatutDossierRepository {
    private EnumMap<EnumStatuts, TypeStatut> types;
    private TypeStatutComparator typeStatutComparator;

    @Override
    public Optional<TypeStatut> findById(EnumStatuts id) {
        TypeStatut typeStatut = this.types.get(id);
        return Optional.ofNullable(typeStatut);
    }

    @PostConstruct
    public void init() {
        this.types = new EnumMap<EnumStatuts, TypeStatut>(EnumStatuts.class);
        this.types.put(EnumStatuts.DEPOSE, new TypeStatut(EnumStatuts.DEPOSE, 0, true, "déposé", 7));
        this.types.put(EnumStatuts.QUALIFIE, new TypeStatut(EnumStatuts.QUALIFIE, 1, true, "qualifié", 10));
        this.types.put(EnumStatuts.INCOMPLET, new TypeStatut(EnumStatuts.INCOMPLET, 1, false, "incomplet", 10));
        this.types.put(EnumStatuts.COMPLET, new TypeStatut(EnumStatuts.COMPLET, 2, true, "complet", 0));
        this.types.put(EnumStatuts.DECISION, new TypeStatut(EnumStatuts.DECISION, 3, true, "décidé", 0));
        this.typeStatutComparator = new TypeStatutComparator();
    }

    @Override
    public List<TypeStatut> findAllGreaterThan(TypeStatut otherType) {
        return this.types.values().stream().filter(type -> this.typeStatutComparator.compare(type, otherType) > 0).collect(Collectors.toList());
    }

}
