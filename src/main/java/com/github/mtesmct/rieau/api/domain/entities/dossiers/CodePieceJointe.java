package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

import java.util.Objects;

public class CodePieceJointe implements ValueObject<CodePieceJointe> {
  private EnumTypes type;
  private String numero;

  public EnumTypes type() {
    return this.type;
  }

  public String numero() {
    return this.numero;
  }

  @Override
  public boolean hasSameValuesAs(final CodePieceJointe other) {
    return other != null && Objects.equals(this.type, other.type) && Objects.equals(this.numero, other.numero);
  }

  public boolean isCerfa() {
    return Objects.equals(this.numero, "0");
  }

  public boolean isDecision() {
    return Objects.equals(this.numero, "d");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    CodePieceJointe other = (CodePieceJointe) o;
    return this.hasSameValuesAs(other);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.type, this.numero);
  }

  @Override
  public String toString() {
    return "CodePieceJointe={ type={" + Objects.toString(this.type) + ", numero={" + this.numero + "} }";
  }

  public CodePieceJointe(final EnumTypes type, final String numero) {
    if (type == null)
      throw new NullPointerException("Le type de la pièce jointe ne peut pas être nul");
    this.type = type;
    if (numero == null || numero.isBlank())
      throw new NullPointerException("Le numero de la pièce jointe ne peut pas être nul ou vide");
    this.numero = numero;
  }

}