package com.github.mtesmct.rieau.api.domain.entities.personnes;

import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public class PersonnePhysiqueId implements ValueObject<PersonnePhysiqueId> {

  private String id;

  @Override
  public boolean hasSameValuesAs(PersonnePhysiqueId other) {
    return other != null && this.id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    PersonnePhysiqueId other = (PersonnePhysiqueId) o;
    return this.hasSameValuesAs(other);
  }

  @Override
  public String toString() {
    return this.id.toString();
  }

  public PersonnePhysiqueId(final String id) {
    if (id == null)
      throw new NullPointerException("L'id de la personne ne peut être nul");
    this.id = id;
  }

}