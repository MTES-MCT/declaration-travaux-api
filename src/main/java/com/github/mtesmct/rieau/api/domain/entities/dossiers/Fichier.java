package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.io.InputStream;
import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;

public class Fichier implements ValueObject<Fichier> {

  private String nom;
  private String mimeType;
  private InputStream content;

  public String nom() {
    return this.nom;
  }
  public InputStream content() {
    return this.content;
  }

  public String mimeType() {
    return this.mimeType;
  }

  @Override
  public boolean hasSameValuesAs(Fichier other) {
    return other != null && this.nom.equals(other.nom) && this.content.equals(other.content) && this.mimeType.equals(other.mimeType);
  }

  public Fichier(final String nom, final String mimeType, final InputStream content) {
    if (nom == null || nom.isBlank())
      throw new NullPointerException("Le nom du fichier ne peut être nul ou blanc");
    this.nom = nom;
    if (mimeType == null || mimeType.isBlank())
      throw new NullPointerException("Le type MIME du fichier ne peut être nul ou blanc");
    this.mimeType = mimeType;
    if (content == null)
      throw new NullPointerException("Le contenu binaire du fichier ne peut être nul");
    this.content = content;
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
    return this.nom;
  }

}