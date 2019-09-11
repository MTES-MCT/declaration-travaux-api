package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public enum StatutDossier implements ValueObject<StatutDossier> {
    DEPOSE, INSTRUCTION, INCOMPLET, CONSULTATIONS, COMPLET, CLOS;

    @Override
    public boolean hasSameValuesAs(final StatutDossier other) {
        return this.equals(other);
    }
    
}