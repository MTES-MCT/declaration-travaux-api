package com.github.mtesmct.rieau.api.domain.entities.dossiers;

public enum EnumTypes {
    DPMI("Déclaration préalable pour une maison individuelle"), PCMI("Permis de construire pour une maison individuelle");
    private String libelle;

    private EnumTypes(String libelle) {
        this.libelle = libelle;
    }

    public String libelle() {
        return libelle;
    }
}