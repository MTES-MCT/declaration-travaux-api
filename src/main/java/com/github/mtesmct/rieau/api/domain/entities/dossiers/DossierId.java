package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.io.Serializable;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public class DossierId implements ValueObject<DossierId>, Serializable {

  private static final long serialVersionUID = 1L;
  private String id;

  @Override
  public boolean hasSameValuesAs(DossierId other) {
    return other != null && this.id.equals(other.id);
  }

  public DossierId(final String id) {
    if (id == null || id.isEmpty())
      throw new NullPointerException("L'id du dépôt ne peut être nul ou vide");
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    DossierId other = (DossierId) o;
    return this.hasSameValuesAs(other);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return this.id;
  }

}