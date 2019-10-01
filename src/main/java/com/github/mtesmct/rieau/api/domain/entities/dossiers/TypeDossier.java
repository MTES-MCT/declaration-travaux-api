package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.List;
import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public class TypeDossier implements ValueObject<TypeDossier> {
    private String code;
    private List<String> piecesAJoindre;
    private TypesDossier type;

    public TypeDossier(TypesDossier type, String code, List<String> piecesAJoindre) {
        this.type = type;
        this.code = code;
        this.piecesAJoindre = piecesAJoindre;
    }

    public TypesDossier type(){
        return this.type;
    }

    public List<String> piecesAJoindre(){
        return this.piecesAJoindre;
    }
    
    public String code(){
        return this.code;
    }

    @Override
    public boolean hasSameValuesAs(TypeDossier other) {
        return other != null && Objects.equals(this.type, other.type) && Objects.equals(this.code, other.code) && Objects.equals(this.piecesAJoindre, other.piecesAJoindre);
    }
    
    @Override
    public String toString() {
        return "TypeDossier={ type={" + Objects.toString(this.type) + "}, totalPiecesAJoindre={" + Objects.toString(this.piecesAJoindre) + "}, code={" + this.code + "} }";
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object)
            return true;
        if (object == null || getClass() != object.getClass())
            return false;
        final TypeDossier other = (TypeDossier) object;
        return this.hasSameValuesAs(other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type,this.code,this.piecesAJoindre);
    }
}