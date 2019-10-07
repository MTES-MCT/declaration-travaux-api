package com.github.mtesmct.rieau.api.domain.factories;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.Factory;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Adresse;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Commune;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Localisation;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Nature;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.domain.services.CommuneService;

@Factory
public class ProjetFactory {

    private CommuneService communeService;

    public ProjetFactory(CommuneService communeService){
        if (communeService == null)
            throw new NullPointerException("Le service des communes ne peut pas être nul.");
        this.communeService = communeService;
    }
    public Projet creer(String numero, String voie, String lieuDit, String codePostal, String bp, String cedex, ParcelleCadastrale parcelleCadastrale, boolean nouvelleConstruction) throws CommuneNotFoundException {
        Optional<Commune> commune = this.communeService.findByCodeCodePostal(codePostal);
        if (commune.isEmpty())
            throw new CommuneNotFoundException(codePostal);
        return new Projet(new Localisation(new Adresse(numero, voie, lieuDit, commune.get(), bp, cedex), parcelleCadastrale), new Nature(nouvelleConstruction));
    }
}