package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.Entity;

public class TypeStatut implements Entity<TypeStatut, EnumStatuts> {
    private EnumStatuts id;
    private Integer ordre;
    private String libelle;
    private Boolean unique;
    private Integer joursDelais;

    @Override
    public EnumStatuts identity() {
        return this.id;
    }

    public Integer ordre() {
        return this.ordre;
    }

    public String libelle() {
        return this.libelle;
    }

    public Integer joursDelais() {
        return this.joursDelais;
    }

    public Boolean unique() {
        return this.unique;
    }

    public TypeStatut(EnumStatuts id, Integer ordre, Boolean unique, String libelle, Integer joursDelais) {
        if (id == null)
            throw new NullPointerException("L'id du type de statut de dossier ne peut pas être nul.");
        this.id = id;
        if (ordre == null)
            throw new NullPointerException("L'ordre du type de statut de dossier ne peut pas être nul.");
        this.ordre = ordre;
        if (unique == null)
            throw new NullPointerException("L'unicité du type de statut de dossier ne peut pas être nul.");
        this.unique = unique;
        if (libelle == null)
            throw new NullPointerException("Le libellé du type de statut de dossier ne peut pas être nul.");
        this.libelle = libelle;
        if (joursDelais == null)
            throw new NullPointerException("Le délai en jours du type de statut de dossier ne peut pas être nul.");
        this.joursDelais = joursDelais;
    }

    @Override
    public boolean hasSameIdentityAs(TypeStatut other) {
        return other != null && Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "TypeStatut={ id={" + Objects.toString(this.id) + "}, unique={" + Objects.toString(this.unique)
                + "}, ordre={" + Objects.toString(this.ordre) + "}, libellé={" + Objects.toString(this.libelle)
                + "}, délai en jours={" + Objects.toString(this.joursDelais) + "} }";
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        final TypeStatut other = (TypeStatut) object;
        return this.hasSameIdentityAs(other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

}