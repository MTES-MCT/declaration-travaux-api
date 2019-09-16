package com.github.mtesmct.rieau.api.domain.entities.dossiers;

import java.util.List;
import java.util.Objects;

import com.github.mtesmct.rieau.api.domain.entities.ValueObject;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Naissance;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonneId;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Sexe;

public class Petitionnaires implements ValueObject<Petitionnaires> {

    private List<Personne> personnes;
    private Dossier dossier;

    @Override
    public boolean hasSameValuesAs(Petitionnaires other) {
        return other != null && dossier.equals(other.dossier) && personnes.equals(other.personnes);
    }

    public Petitionnaires(Dossier dossier){
        if (dossier == null)
          throw new NullPointerException("Le dossier à rattacher aux pétitionnaires ne peut pas être nul");
        this.dossier = dossier;
    }

    void ajouter(final String personneId, String email, String nom, String prenom, Sexe sexe, Naissance naissance){
        this.personnes.add(new Personne(new PersonneId(personneId), email, nom, prenom, sexe, naissance));
    }


  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
      Petitionnaires other = (Petitionnaires) o;
    return this.hasSameValuesAs(other);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dossier,personnes);
  }

  @Override
  public String toString() {
    return "Petitionnaires=[dossier=" +this.dossier.toString() + ", personnes=" + this.personnes.toString() + "]";
  }

}