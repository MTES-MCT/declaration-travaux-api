package com.github.mtesmct.rieau.api.domain.entities.personnes;

import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.Entity;

public class User implements Entity<User, PersonneId> {
    private Personne identite;
    private String[] profils;

    public Personne identite() {
        return this.identite;
    }

    public String[] profils() {
        return this.profils;
    }

    @Override
    public String toString() {
        return "User={ identite={" + Objects.toString(this.identite) + "}, profils={" + String.join(",", this.profils)
                + "} }";
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.identite);
    }

    @Override
    public boolean hasSameIdentityAs(User other) {
        return other != null && this.identite != null && this.identite.equals(other.identite);
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        final User other = (User) object;
        return this.hasSameIdentityAs(other);
    }

    public User(final Personne identite, final String[] profils) {
        if (identite == null)
            throw new NullPointerException("L'identité de l'utilisateur ne peut pas être nulle.");
        this.identite = identite;
        if (profils == null || profils.length < 1)
            throw new NullPointerException("Les profils de l'utilisateur ne peuvent pas être nuls ou vide.");
        this.profils = profils;
    }

    @Override
    public PersonneId identity() {
        return this.identite.identity();
    }
}