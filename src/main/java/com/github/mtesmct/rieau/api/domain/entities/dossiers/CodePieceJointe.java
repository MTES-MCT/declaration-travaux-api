package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public class CodePieceJointe implements ValueObject<CodePieceJointe> {
    private TypesDossier type;
    private String numero;

    public TypesDossier type(){
      return this.type;
    }
    public String numero(){
      return this.numero;
    }

    @Override
    public boolean hasSameValuesAs(final CodePieceJointe other) {
        return other!= null && this.type.equals(other.type) && this.numero.equals(other.numero);
    }

    public boolean isCerfa() {
        return this.numero.equals("0");
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
      return this.type.toString()+this.numero;
    }

    public CodePieceJointe(final TypesDossier type, final String numero) {
        if (type == null)
            throw new NullPointerException("Le type de la pièce jointe ne peut pas être nul");
        this.type = type;
        if (numero == null || numero.isBlank())
            throw new NullPointerException("Le numero de la pièce jointe ne peut pas être nul ou vide");
        this.numero = numero;
    }
    
}