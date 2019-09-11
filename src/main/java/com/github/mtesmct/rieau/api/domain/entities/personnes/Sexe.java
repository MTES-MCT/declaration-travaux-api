package com.github.mtesmct.rieau.api.domain.entities.personnes;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public enum Sexe implements ValueObject<Sexe> {
    HOMME, FEMME;

    public String civilite(){
        if (this.equals(HOMME)) return "Monsieur";
        if (this.equals(FEMME)) return "Madame";
        return "";
    }

    @Override
    public boolean hasSameValuesAs(final Sexe other) {
        return this.equals(other);
    }
    
}