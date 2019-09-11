package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public enum TypePieceJointe implements ValueObject<TypePieceJointe> {
    CERFA, DP, PCMI;

    @Override
    public boolean hasSameValuesAs(final TypePieceJointe other) {
        return this.equals(other);
    }
    
}