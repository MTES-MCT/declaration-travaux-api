package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public class Commune implements ValueObject<Commune> {

    private String codePostal;
    private String nom;
    private String department;

    public String codePostal(){
        return this.codePostal;
    }
    public String nom(){
        return this.nom;
    }
    public String department(){
        return this.department;
    }

    @Override
    public boolean hasSameValuesAs(Commune other) {
        return other != null && Objects.equals(this.codePostal, other.codePostal)
                && Objects.equals(this.nom, other.nom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.codePostal, this.nom);
    }

    @Override
    public String toString() {
        return "Commune={ nom={" + this.nom + "}, code postal={" + this.codePostal + "} }";
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