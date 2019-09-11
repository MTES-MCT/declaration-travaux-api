package com.github.mtesmct.rieau.api.domain.entities.personnes;

import java.util.Date;
import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public class Naissance implements ValueObject<Naissance> {

  private Date date;
  private String commune;

  public Date date(){
    return this.date;
  }
  public String commune(){
    return this.commune;
  }

  @Override
  public int hashCode() {
    return Objects.hash(date, commune);
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
    return "Naissance=[date=" + this.date.toString() + ", commune=" + this.commune + "]";
  }

  @Override
  public boolean hasSameValuesAs(Naissance other) {
    return other != null && this.date.equals(other.date) && this.commune.equals(other.commune);
  }

  public Naissance(final Date date, final String commune) {
    if (date == null)
      throw new NullPointerException("Le date de naissance ne peut être nul");
    this.date = date;
    if (commune == null || commune.isEmpty())
      throw new NullPointerException("Le commune de naissance ne peut être nulle ou vide");
    this.commune = commune;
  }

}