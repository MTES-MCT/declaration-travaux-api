package com.github.mtesmct.rieau.api.domain.entities.dossiers;

public enum EnumStatuts {
    DEPOSE(0, true, "déposé"), QUALIFIE(1, true, "qualifié"), INSTRUCTION(2, false, "en cours d'instruction"),
    INCOMPLET(2, false, "incomplet"), COMPLET(3, true, "complet"), CONSULTATIONS(4, true, "consultations en cours"),
    DECISION(5, true, "décision prise");

    private int ordre;
    private boolean unique;
    private String libelle;

    public int ordre() {
        return this.ordre;
    }

    public boolean unique() {
        return this.unique;
    }

    public String libelle() {
        return this.libelle;
    }

    private EnumStatuts(int ordre, boolean unique, String libelle) {
        this.ordre = ordre;
        this.unique = unique;
        this.libelle = libelle;
    }
}