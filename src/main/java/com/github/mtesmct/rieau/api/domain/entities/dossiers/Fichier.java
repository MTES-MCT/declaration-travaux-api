package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.io.InputStream;
import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public class Fichier implements ValueObject<Fichier> {

  private String nom;
  private String mimeType;
  private long size;
  private InputStream content;

  public String nom() {
    return this.nom;
  }
  public InputStream content() {
    return this.content;
  }
  public long size() {
    return this.size;
  }

  public String mimeType() {
    return this.mimeType;
  }

  @Override
  public boolean hasSameValuesAs(Fichier other) {
    return other != null && Objects.equals(this.nom, other.nom) && Objects.equals(this.content, other.content) && Objects.equals(this.mimeType, other.mimeType) && this.size == other.size;
  }

  public Fichier(final String nom, final String mimeType, final InputStream content, final long size) {
    if (Objects.toString(nom).equals("null"))
      throw new NullPointerException("Le nom du fichier ne peut pas être nul");
    this.nom = nom;
    if (mimeType == null || mimeType.isBlank())
      throw new NullPointerException("Le type MIME du fichier ne peut être nul ou blanc");
    this.mimeType = mimeType;
    if (content == null)
      throw new NullPointerException("Le contenu binaire du fichier ne peut être nul");
    this.content = content;
    if (size < 1)
      throw new IllegalArgumentException("La taille du fichier doit être strictement supérieure à 0");
    this.size = size;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Fichier other = (Fichier) o;
    return this.hasSameValuesAs(other);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.nom, this.mimeType);
  }

  @Override
  public String toString() {
    return "Fichier={ nom={" + this.nom + "}, mimeType={" + this.mimeType + "}, size={" + this.size + "} }";
  }

}