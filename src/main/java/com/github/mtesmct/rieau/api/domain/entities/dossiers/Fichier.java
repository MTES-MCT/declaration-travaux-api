package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.Entity;

public class Fichier implements Entity<Fichier, FichierId> {

  private FichierId id;
  private String nom;
  private String mimeType;
  private long taille;
  private InputStream contenu;

  public String nom() {
    return this.nom;
  }

  public InputStream contenu() {
    return this.contenu;
  }

  public long taille() {
    return this.taille;
  }

  public String mimeType() {
    return this.mimeType;
  }

  public void fermer() throws IOException {
    if (this.contenu != null) this.contenu.close();
  }

  public Fichier(final FichierId id, final String nom, final String mimeType, final InputStream contenu, final long taille) {
    if (id == null)
      throw new NullPointerException("L'id du fichier ne peut pas être nul");
    this.id = id;
    if (Objects.toString(nom).equals("null"))
      throw new NullPointerException("Le nom du fichier ne peut pas être nul");
    this.nom = nom;
    if (mimeType == null || mimeType.isBlank())
      throw new NullPointerException("Le type MIME du fichier ne peut être nul ou blanc");
    this.mimeType = mimeType;
    if (contenu == null)
      throw new NullPointerException("Le contenu du fichier ne peut être nul");
    this.contenu = contenu;
    if (taille < 1)
      throw new IllegalArgumentException("La taille du fichier doit être strictement supérieure à 0");
    this.taille = taille;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Fichier other = (Fichier) o;
    return this.hasSameIdentityAs(other);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  @Override
  public String toString() {
    return "Fichier={ id={" + Objects.toString(this.id) + "}, nom={" + this.nom + "}, mimeType={" + this.mimeType + "}, taille={" + this.taille + "} }";
  }

  @Override
  public FichierId identity() {
    return this.id;
  }

  @Override
  public boolean hasSameIdentityAs(Fichier other) {
    return other != null && Objects.equals(this.id, other.id);
  }

}