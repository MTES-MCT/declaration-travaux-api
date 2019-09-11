package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public class CodePieceJointe implements ValueObject<CodePieceJointe> {
    private TypePieceJointe type;
    private Integer numero;

    public TypePieceJointe type(){
      return this.type;
    }

    @Override
    public boolean hasSameValuesAs(final CodePieceJointe other) {
        return other!= null && this.type.equals(other.type) && this.numero.equals(other.numero);
    }

    public boolean isCerfa() {
        return this.type.equals(TypePieceJointe.CERFA);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;  
      CodePieceJointe other = (CodePieceJointe) o;  
      return this.hasSameValuesAs(other);
    }
  
    @Override
    public int hashCode() {
      return Objects.hash(type, numero);
    }
  
    @Override
    public String toString() {
      return this.type.toString()+this.numero.toString();
    }

    public CodePieceJointe(final TypePieceJointe type, final Integer numero) {
        if (type == null)
            throw new NullPointerException("Le type de pièce jointe ne peut être nul");
        this.type = type;
        if (!type.equals(TypePieceJointe.CERFA) && numero == null)
            throw new NullPointerException("Le numero de pièce jointe ne peut être nul");
        this.numero = numero;
    }
    
}