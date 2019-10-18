package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public class Nature implements ValueObject<Nature> {

    private boolean nouvelleConstruction;

    public boolean nouvelleConstruction() {
        return this.nouvelleConstruction;
    }

    @Override
    public boolean hasSameValuesAs(Nature other) {
        return other!=null && Objects.equals(this.nouvelleConstruction, other.nouvelleConstruction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.nouvelleConstruction);
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        final Nature other = (Nature) object;
        return this.hasSameValuesAs(other);
    }

    @Override
    public String toString() {
        return "Nature={ nouvelle construction={" + Objects.toString(this.nouvelleConstruction) + "} }";
    }

    public Nature() {
        this.nouvelleConstruction = false;
    }

    public Nature(boolean nouvelleConstruction) {
        this.nouvelleConstruction = nouvelleConstruction;
    }

}