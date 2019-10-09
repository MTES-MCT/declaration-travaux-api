package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.Entity;

public class Commune implements Entity<Commune, String> {

    private String codePostal;
    private String nom;
    private String department;

    public String codePostal() {
        return this.codePostal;
    }

    public String nom() {
        return this.nom;
    }

    public String department() {
        return this.department;
    }

    @Override
    public String identity() {
        return this.codePostal;
    }

    @Override
    public boolean hasSameIdentityAs(Commune other) {
        return other != null && Objects.equals(this.identity(), other.identity());
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        final Commune other = (Commune) object;
        return this.hasSameIdentityAs(other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.identity());
    }

    @Override
    public String toString() {
        return "Commune={ nom={" + this.nom + "}, code postal={" + this.codePostal + "}, département={"
                + this.department + "} }";
    }

    public Commune(String codePostal, String nom, String department) {
        if (codePostal == null)
            throw new NullPointerException("Le code postal de la commune ne peut pas être nul");
        this.codePostal = codePostal;
        if (nom == null)
            throw new NullPointerException("Le nom de la commune ne peut pas être nul");
        this.nom = nom;
        if (department == null)
            throw new NullPointerException("Le départment de la commune ne peut pas être nul");
        this.department = department;
    }

}