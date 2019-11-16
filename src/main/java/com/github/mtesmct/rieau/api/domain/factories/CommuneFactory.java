package com.github.mtesmct.rieau.api.domain.factories;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.Factory;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Commune;
import com.github.mtesmct.rieau.api.domain.services.CommuneService;
import com.github.mtesmct.rieau.api.domain.services.StringExtractService;

@Factory
public class CommuneFactory {
    private StringExtractService stringExtractService;
    private CommuneService communeService;

    public CommuneFactory(StringExtractService stringExtractService, CommuneService communeService) {
        if (stringExtractService == null)
            throw new NullPointerException("Le service des string extract ne peut pas être nul.");
        this.stringExtractService = stringExtractService;
        if (communeService == null)
            throw new NullPointerException("Le service des communes ne peut pas être nul.");
        this.communeService = communeService;
    }

    public Optional<Commune> parse(String texte) throws CommuneParseException {
        Optional<Commune> commune = Optional.empty();
        Optional<String> communeString = this.stringExtractService.entityExtract("Commune", texte);
        if (communeString.isEmpty())
            throw new CommuneParseException(texte);
        Optional<String> codePostal = this.stringExtractService.attributeExtract("codePostal", texte);
        if (codePostal.isEmpty())
            throw new CommuneParseException(texte);
        commune = this.communeService.findByCodeCodePostal(codePostal.get());
        if (commune.isEmpty())
            throw new CommuneParseException(texte);
        return commune;
    }

}