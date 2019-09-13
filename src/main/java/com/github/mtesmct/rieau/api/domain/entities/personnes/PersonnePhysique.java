package com.github.mtesmct.rieau.api.domain.entities.personnes;

import java.util.Objects;
import java.util.regex.Pattern;

import com.github.mtesmct.rieau.api.domain.entities.Entity;

public class PersonnePhysique implements Entity<PersonnePhysique, PersonnePhysiqueId> {
    public static final String EMAIL_REGEXP = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    private PersonnePhysiqueId id;
    private String email;
    private Sexe sexe;
    private String nom;
    private String prenom;
    private Naissance naissance;

    public String email(){
        return this.email;
    }

    public String nom(){
        return this.nom;
    }

    public String prenom(){
        return this.prenom;
    }

    public Sexe sexe(){
        return this.sexe;
    }

    public Naissance naissance(){
        return this.naissance;
    }

    @Override
    public String toString() {
        return this.id.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean hasSameIdentityAs(PersonnePhysique other) {
        return other != null && this.id.hasSameValuesAs(other.id);
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        final PersonnePhysique other = (PersonnePhysique) object;
        return this.hasSameIdentityAs(other);
    }

    public PersonnePhysique(final String personnePhysiqueId, final String email){
        if (personnePhysiqueId == null || personnePhysiqueId.isBlank())
            throw new NullPointerException("L'id de la personne ne peut pas être nul ou vide");
        this.id = new PersonnePhysiqueId(personnePhysiqueId);
        if (email == null || email.isEmpty())
            throw new NullPointerException("L'email de la personne ne peut être nul ou vide");
        if (!Pattern.compile(EMAIL_REGEXP).matcher(email).matches())
            throw new IllegalArgumentException("L'email de la personne est non conforme RFC 5322");
        this.email = email;
    }

    public PersonnePhysique(final PersonnePhysiqueId id, String email, String nom, String prenom, Sexe sexe, Naissance naissance) {
        if (id == null)
            throw new NullPointerException("L'id de la personne ne peut être nul");
        this.id = id;
        if (email == null || email.isEmpty())
            throw new NullPointerException("L'email de la personne ne peut être nul ou vide");
        if (!Pattern.compile(EMAIL_REGEXP).matcher(email).matches())
            throw new IllegalArgumentException("L'email de la personne est non conforme RFC 5322");
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.sexe = sexe;
        this.naissance = naissance;
    }

    @Override
    public PersonnePhysiqueId identity() {
        return this.id;
    }
}