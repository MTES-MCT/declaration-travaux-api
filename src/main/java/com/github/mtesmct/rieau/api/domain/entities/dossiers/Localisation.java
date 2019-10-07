package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public class Localisation implements ValueObject<Localisation> {

    private Adresse adresse;
    private List<ParcelleCadastrale> parcellesCadastrales;

    public Adresse adresse() {
        return this.adresse;
    }

    public List<ParcelleCadastrale> parcellesCadastrales() {
        return this.parcellesCadastrales;
    }

    @Override
    public boolean hasSameValuesAs(Localisation other) {
        return other != null && Objects.equals(this.adresse, other.adresse)
                && Objects.equals(this.parcellesCadastrales, other.parcellesCadastrales);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.adresse, this.parcellesCadastrales);
    }

    @Override
    public String toString() {
        return "Localisation={ adresse={" + Objects.toString(this.adresse) + "}, cadastres={"
                + Objects.toString(this.parcellesCadastrales) + "} }";
    }

    public Localisation(Adresse adresse, ParcelleCadastrale parcelle) {
        if (adresse == null)
            throw new NullPointerException("L'adresse de la localisation ne peut pas être nulle");
        this.adresse = adresse;
        if (parcelle == null)
            throw new NullPointerException("La localisation doit contenir au moins une référence cadastrale non nulle");
        this.parcellesCadastrales = new ArrayList<ParcelleCadastrale>();
        this.parcellesCadastrales.add(parcelle);
    }

    public void ajouterParcelle(ParcelleCadastrale parcelle) {
        if (this.parcellesCadastrales == null)
            this.parcellesCadastrales = new ArrayList<ParcelleCadastrale>();
        this.parcellesCadastrales.add(parcelle);
    }

}