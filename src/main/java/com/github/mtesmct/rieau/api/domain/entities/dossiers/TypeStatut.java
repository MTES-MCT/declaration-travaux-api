package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public class TypeStatut implements ValueObject<TypeStatut> {
    private EnumStatuts statut;
    private Integer joursDelais;

    public Integer joursDelais() {
        return this.joursDelais;
    }

    public EnumStatuts statut() {
        return this.statut;
    }

    public TypeStatut(EnumStatuts statut, Integer joursDelais) {
        if (statut == null)
            throw new NullPointerException("Le statut ne peut pas être nul.");
        this.statut = statut;
        this.joursDelais = joursDelais;
    }

    @Override
    public boolean hasSameValuesAs(TypeStatut other) {
        return other != null && Objects.equals(this.statut, other.statut);
    }

    @Override
    public String toString() {
        return "TypeStatut={ statut={" + Objects.toString(this.statut) + "}, délai en jours={" + Objects.toString(this.joursDelais) + "} }";
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        final TypeStatut other = (TypeStatut) object;
        return this.hasSameValuesAs(other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.statut);
    }

    public int ordre() {
        return this.statut.ordre();
    }

}