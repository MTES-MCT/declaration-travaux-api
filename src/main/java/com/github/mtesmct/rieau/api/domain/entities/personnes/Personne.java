package com.github.mtesmct.rieau.api.domain.entities.personnes;

import java.util.Objects;
import java.util.regex.Pattern;

import com.github.mtesmct.rieau.api.domain.entities.Entity;

public class Personne implements Entity<Personne, PersonneId> {
    public static final String EMAIL_REGEXP = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    private PersonneId id;
    private String email;
    private Sexe sexe;
    private String nom;
    private String prenom;
    private Naissance naissance;

    public String email() {
        return this.email;
    }

    public String nom() {
        return this.nom;
    }

    public String prenom() {
        return this.prenom;
    }

    public Sexe sexe() {
        return this.sexe;
    }

    public Naissance naissance() {
        return this.naissance;
    }

    @Override
    public String toString() {
        return "Personne={ id={" + this.id.toString() + "}, prenom={" + this.prenom + "}, nom={" + this.nom
                + "}, sexe={" + this.sexe != null ? this.sexe.toString() : "null" + "}, email={" + this.email + "}, naissance={"
                + this.naissance != null ? this.naissance.toString() : "null" + "} }";
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean hasSameIdentityAs(Personne other) {
        return other != null && this.id != null && this.id.equals(other.id);
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        final Personne other = (Personne) object;
        return this.hasSameIdentityAs(other);
    }

    public Personne(final String personneId, final String email) {
        if (personneId == null || personneId.isBlank())
            throw new NullPointerException("L'id de la personne ne peut pas être nul ou vide");
        this.id = new PersonneId(personneId);
        if (email == null || email.isEmpty())
            throw new NullPointerException("L'email de la personne ne peut être nul ou vide");
        if (!Pattern.compile(EMAIL_REGEXP).matcher(email).matches())
            throw new IllegalArgumentException("L'email de la personne est non conforme RFC 5322");
        this.email = email;
    }

    public Personne(final String personneId, String email, String nom, String prenom, Sexe sexe, Naissance naissance) {
        this(personneId, email);
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.sexe = sexe;
        this.naissance = naissance;
    }

    @Override
    public PersonneId identity() {
        return this.id;
    }
}