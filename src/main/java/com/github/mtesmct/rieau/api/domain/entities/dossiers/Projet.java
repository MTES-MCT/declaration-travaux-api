package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public class Projet implements ValueObject<Projet> {
    private Localisation localisation;
    private Nature nature;

    public Localisation localisation() {
        return this.localisation;
    }

    public Nature nature() {
        return this.nature;
    }

    @Override
    public boolean hasSameValuesAs(Projet other) {
        return other != null && Objects.equals(other.nature, this.nature)
                && Objects.equals(other.localisation, this.localisation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.nature, this.localisation);
    }

    @Override
    public String toString() {
        return "Projet={ nature={" + Objects.toString(this.nature) + "}, localisation={"
                + Objects.toString(this.localisation) + "} }";
    }

    public Projet(Localisation localisation, Nature nature) {
        if (localisation == null)
            throw new NullPointerException("La localisation du projet ne peut pas être nulle");
        this.localisation = localisation;
        if (nature == null)
            throw new NullPointerException("La nature du projet ne peut pas être nulle");
        this.nature = nature;
    }

}