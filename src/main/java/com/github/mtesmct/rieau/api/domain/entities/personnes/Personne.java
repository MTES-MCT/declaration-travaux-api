package com.github.mtesmct.rieau.api.domain.entities.personnes;

import java.util.Objects;
import java.util.regex.Pattern;

import com.github.mtesmct.rieau.api.domain.entities.Entity;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Adresse;

public class Personne implements Entity<Personne, PersonneId> {
    public static final String EMAIL_REGEXP = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    private PersonneId id;
    private String email;
    private Sexe sexe;
    private String nom;
    private String prenom;
    private Adresse adresse;

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

    public Adresse adresse() {
        return this.adresse;
    }

    @Override
    public String toString() {
        return "Personne={ id={" + Objects.toString(this.id) + "}, prenom={" + this.prenom + "}, nom={" + this.nom
                + "}, sexe={" + Objects.toString(this.sexe) + "}, email={" + this.email + "}, adresse={"
                + Objects.toString(this.adresse) + "} }";
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
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

    public Personne(final String personneId, final String nom, final String prenom) {
        if (personneId == null || personneId.isBlank())
            throw new NullPointerException("L'id de la personne ne peut pas être nul ou vide");
        this.id = new PersonneId(personneId);
        this.nom = nom;
        this.prenom = prenom;
    }

    public Personne(final String personneId, final String nom, final String prenom, final Sexe sexe, final String email, final Adresse adresse) {
        this(personneId, nom, prenom);
        if (!Pattern.compile(EMAIL_REGEXP).matcher(email).matches())
            throw new IllegalArgumentException("L'email de la personne est non conforme RFC 5322");
        this.email = email;
        this.sexe = sexe;
        this.adresse = adresse;
    }

    @Override
    public PersonneId identity() {
        return this.id;
    }
}