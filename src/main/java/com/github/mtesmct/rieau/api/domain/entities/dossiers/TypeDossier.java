package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public enum TypeDossier implements ValueObject<TypeDossier> {
    DP("13406"), PCMI("13703");

    private final String code;

    private TypeDossier(String code){
        this.code = code;
    }

    public String getCode(){
        return this.code;
    }

    @Override
    public boolean hasSameValuesAs(final TypeDossier other) {
        return this.equals(other);
    }
    
}