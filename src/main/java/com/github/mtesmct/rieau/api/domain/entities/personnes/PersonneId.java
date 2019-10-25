package com.github.mtesmct.rieau.api.domain.entities.personnes;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

import java.util.Objects;

public class PersonneId implements ValueObject<PersonneId> {

  private String id;

  @Override
  public boolean hasSameValuesAs(PersonneId other) {
    return other != null && Objects.equals(this.id, other.id);
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
    PersonneId other = (PersonneId) o;
    return this.hasSameValuesAs(other);
  }

  @Override
  public String toString() {
    return Objects.toString(this.id);
  }

  public PersonneId(final String id) {
    if (id == null)
      throw new NullPointerException("L'id de la personne ne peut pas être nul");
    this.id = id;
  }

}