package com.github.mtesmct.rieau.api.infra.persistence.inmemory.repositories;

import java.util.EnumMap;
import java.util.Optional;

import javax.annotation.PostConstruct;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumStatuts;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatut;
import com.github.mtesmct.rieau.api.domain.repositories.TypeStatutDossierRepository;

import org.springframework.stereotype.Component;

@Component
public class InMemoryStatutDossierRepository implements TypeStatutDossierRepository {
    private EnumMap<EnumStatuts, TypeStatut> types;

    @Override
    public Optional<TypeStatut> findByStatut(EnumStatuts statut) {
        TypeStatut typeStatut = this.types.get(statut);
        return Optional.ofNullable(typeStatut);
    }
    
    @PostConstruct
    public void initJoursDelais() {        
        this.types = new EnumMap<EnumStatuts, TypeStatut>(EnumStatuts.class);
        this.types.put(EnumStatuts.DEPOSE, new TypeStatut(EnumStatuts.DEPOSE,7));
        this.types.put(EnumStatuts.QUALIFIE, new TypeStatut(EnumStatuts.QUALIFIE,10));
        this.types.put(EnumStatuts.INSTRUCTION, new TypeStatut(EnumStatuts.INSTRUCTION,30));
        this.types.put(EnumStatuts.INCOMPLET, new TypeStatut(EnumStatuts.INCOMPLET,10));
        this.types.put(EnumStatuts.CONSULTATIONS, new TypeStatut(EnumStatuts.CONSULTATIONS,10));
        this.types.put(EnumStatuts.COMPLET, new TypeStatut(EnumStatuts.COMPLET,0));
        this.types.put(EnumStatuts.DECISION, new TypeStatut(EnumStatuts.DECISION,0));
    }

}
