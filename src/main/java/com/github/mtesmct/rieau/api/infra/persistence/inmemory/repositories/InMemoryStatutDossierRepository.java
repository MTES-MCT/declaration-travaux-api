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
    public Optional<TypeStatut> findById(EnumStatuts id) {
        TypeStatut typeStatut = this.types.get(id);
        return Optional.ofNullable(typeStatut);
    }

    @PostConstruct
    public void initJoursDelais() {
        this.types = new EnumMap<EnumStatuts, TypeStatut>(EnumStatuts.class);
        this.types.put(EnumStatuts.DEPOSE, new TypeStatut(EnumStatuts.DEPOSE, 0, true, "déposé", 7));
        this.types.put(EnumStatuts.QUALIFIE, new TypeStatut(EnumStatuts.QUALIFIE, 1, true, "qualifié", 10));
        this.types.put(EnumStatuts.INSTRUCTION, new TypeStatut(EnumStatuts.INSTRUCTION, 2, false, "en cours d'instruction", 30));
        this.types.put(EnumStatuts.INCOMPLET, new TypeStatut(EnumStatuts.INCOMPLET, 2 , false, "incomplet", 10));
        this.types.put(EnumStatuts.COMPLET, new TypeStatut(EnumStatuts.COMPLET,3, true, "complet", 0));
        this.types.put(EnumStatuts.CONSULTATIONS, new TypeStatut(EnumStatuts.CONSULTATIONS, 4, true, "consultations en cours", 10));
        this.types.put(EnumStatuts.DECISION, new TypeStatut(EnumStatuts.DECISION,5, true, "décision prise", 0));
    }

}
