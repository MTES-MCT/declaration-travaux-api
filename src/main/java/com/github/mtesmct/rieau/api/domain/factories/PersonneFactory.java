package com.github.mtesmct.rieau.api.domain.factories;

import com.github.mtesmct.rieau.api.domain.entities.Factory;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Adresse;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Commune;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Naissance;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Sexe;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.domain.services.CommuneService;
import com.github.mtesmct.rieau.api.domain.services.DateService;

import java.util.Optional;

@Factory
public class PersonneFactory {
    private DateService dateService;
    private CommuneService communeService;

    public PersonneFactory(DateService dateService, CommuneService communeService) {
        if (dateService == null)
            throw new NullPointerException("Le service des dates ne peut pas être nul.");
        this.dateService = dateService;
        if (communeService == null)
            throw new NullPointerException("Le service des communes ne peut pas être nul.");
        this.communeService = communeService;
    }

    public Personne creer(String personneId, String email, Sexe sexe, String nom, String prenom, String dateNaissance,
            String communeNaissance, String codePostal, String numero, String voie, String lieuDit, String bp,
            String cedex) throws CommuneNotFoundException {
        if (personneId == null)
            throw new NullPointerException("L'id de la personne ne peut pas être nul.");
        Naissance naissance = null;
        if (dateNaissance != null)
            naissance = new Naissance(this.dateService.parse(dateNaissance), communeNaissance);
        Adresse adresse = null;
        if (codePostal != null) {
            Optional<Commune> commune = this.communeService.findByCodeCodePostal(codePostal);
            if (commune.isEmpty())
                throw new CommuneNotFoundException(codePostal);
            adresse = new Adresse(numero, voie, lieuDit, commune.get(), bp, cedex);
        }
        return new Personne(personneId, email, nom, prenom, sexe, naissance, adresse);
    }
}