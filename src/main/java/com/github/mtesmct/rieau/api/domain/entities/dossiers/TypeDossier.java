package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public class TypeDossier implements ValueObject<TypeDossier> {
    private String code;
    private int totalPiecesAJoindre;
    private TypesDossier type;

    public TypeDossier(TypesDossier type, String code, int totalPiecesAJoindre) {
        this.type = type;
        this.code = code;
        this.totalPiecesAJoindre = totalPiecesAJoindre;
    }

    public List<CodePieceJointe> codesPiecesAJoindre() {
        List<CodePieceJointe>  codesPiecesAJoindre = new ArrayList<CodePieceJointe>();
        for (int i = 1; i <= this.totalPiecesAJoindre; i++){
            codesPiecesAJoindre.add(new CodePieceJointe(this.type, Integer.toString(i)));
        }
        return codesPiecesAJoindre;
    }

    public TypesDossier type(){
        return this.type;
    }

    public String code(){
        return this.code;
    }

    @Override
    public boolean hasSameValuesAs(TypeDossier other) {
        return other != null && Objects.equals(this.type, other.type) && Objects.equals(this.code, other.code) && this.totalPiecesAJoindre == other.totalPiecesAJoindre;
    }
    
    @Override
    public String toString() {
        return "TypeDossier={ type={" + Objects.toString(this.type) + "}, totalPiecesAJoindre={" + this.totalPiecesAJoindre + "}, code={" + this.code + "} }";
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
        return Objects.hash(this.type,this.code,this.totalPiecesAJoindre);
    }
}