package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public class PiecesJointesOptionnelles implements ValueObject<PiecesJointesOptionnelles> {
    private PieceJointe dp1;

    @Override
    public String toString() {
        return this.dp1.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final PiecesJointesOptionnelles other = (PiecesJointesOptionnelles) object;
        return this.hasSameValuesAs(other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.dp1);
    }

    @Override
    public boolean hasSameValuesAs(PiecesJointesOptionnelles other) {
        return other != null && this.dp1.hasSameValuesAs(other.dp1);
    }

    public PiecesJointesOptionnelles() {
    }
}