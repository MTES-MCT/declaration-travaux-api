package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public class Adresse implements ValueObject<Adresse> {

    private String numero;
    private String voie;
    private String lieuDit;
    private Commune commune;
    private String bp;
    private String cedex;

    public String numero() {
        return this.numero;
    }

    public String voie() {
        return this.voie;
    }

    public String lieuDit() {
        return this.lieuDit;
    }

    public Commune commune() {
        return this.commune;
    }

    public String bp() {
        return this.bp;
    }

    public String cedex() {
        return this.cedex;
    }

    @Override
    public boolean hasSameValuesAs(Adresse other) {
        return other != null && Objects.equals(this.numero, other.numero) && Objects.equals(this.voie, other.voie)
                && Objects.equals(this.lieuDit, other.lieuDit) && Objects.equals(this.commune, other.commune);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.numero, this.voie, this.lieuDit, this.commune, this.bp, this.cedex);
    }

    @Override
    public String toString() {
        return "Adresse={ numero={" + this.numero + "}, voie={" + this.voie + "}, lieu-dit={" + this.lieuDit
                + "}, commune={" + Objects.toString(this.commune) + "}, bp={" + this.bp + "}, cedex={" + this.cedex
                + "} }";
    }

    public Adresse(String numero, String voie, String lieuDit, Commune commune, String bp, String cedex) {
        this.numero = numero;
        this.voie = voie;
        this.lieuDit = lieuDit;
        if (commune == null)
            throw new NullPointerException("La commune de l'adresse ne peut pas être nulle.");
        this.commune = commune;
        this.bp = bp;
        this.cedex = cedex;
    }

}