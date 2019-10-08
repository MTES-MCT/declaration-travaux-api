package com.github.mtesmct.rieau.api.infra.http.dossiers;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;

import org.springframework.stereotype.Component;

@Component
public class JsonProjetFactory {

    public JsonProjet toJson(Projet projet) {
        JsonProjet jsonProjet = null;
        if (projet != null) {
            jsonProjet = new JsonProjet(
                    new JsonAdresse(projet.localisation().adresse().commune().codePostal(),
                            projet.localisation().adresse().commune().nom(), projet.localisation().adresse().numero(),
                            projet.localisation().adresse().voie(), projet.localisation().adresse().lieuDit(),
                            projet.localisation().adresse().bp(), projet.localisation().adresse().cedex(),
                            projet.localisation().parcellesCadastrales().stream().map(ParcelleCadastrale::toFlatString).toArray(String[]::new)),
                    projet.nature().nouvelleConstruction(), projet.localisation().lotissement());
        }
        return jsonProjet;
    }
}