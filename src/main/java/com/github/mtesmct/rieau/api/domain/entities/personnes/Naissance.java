package com.github.mtesmct.rieau.api.domain.entities.personnes;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

import java.time.LocalDateTime;
import java.util.Objects;

public class Naissance implements ValueObject<Naissance> {

  private LocalDateTime date;
  private String commune;

  public LocalDateTime date(){
    return this.date;
  }
  public String commune(){
    return this.commune;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.date, this.commune);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Naissance other = (Naissance) o;
    return this.hasSameValuesAs(other);
  }

  @Override
  public String toString() {
    return "Naissance={ date={" + Objects.toString(this.date) + "}, commune={" + this.commune + "} }";
  }

  @Override
  public boolean hasSameValuesAs(Naissance other) {
    return other != null && Objects.equals(this.date, other.date) && Objects.equals(this.commune, other.commune);
  }

  public Naissance(final LocalDateTime date, final String commune) {
    this.date = date;
    this.commune = commune;
  }

}