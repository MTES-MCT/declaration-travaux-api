package com.github.mtesmct.rieau.api.domain.factories;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.Factory;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Adresse;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Commune;
import com.github.mtesmct.rieau.api.domain.services.StringExtractService;

@Factory
public class AdresseFactory {
    private StringExtractService stringExtractService;
    private CommuneFactory communeFactory;

    public AdresseFactory(StringExtractService stringExtractService, CommuneFactory communeFactory) {
        if (stringExtractService == null)
            throw new NullPointerException("Le service des string extract ne peut pas être nul.");
        this.stringExtractService = stringExtractService;
        if (communeFactory == null)
            throw new NullPointerException("La factory des communes ne peut pas être nulle.");
        this.communeFactory = communeFactory;
    }

    public Optional<Adresse> parse(String texte) throws AdresseParseException {
        Optional<Adresse> adresse = Optional.empty();
        Optional<String> adresseString = this.stringExtractService.entityExtract("Adresse", texte);
        if (adresseString.isEmpty())
            throw new AdresseParseException(texte);
        Optional<String> numero = this.stringExtractService.attributeExtract("numero", texte);
        Optional<String> voie = this.stringExtractService.attributeExtract("voie", texte);
        Optional<String> lieuDit = this.stringExtractService.attributeExtract("lieuDit", texte);
        Optional<String> communeString = this.stringExtractService.entityExtract("commune", texte);
        Optional<String> bp = this.stringExtractService.attributeExtract("bp", texte);
        Optional<String> cedex = this.stringExtractService.attributeExtract("cedex", texte);
        Optional<Commune> commune = Optional.empty();
        try {
            commune = this.communeFactory.parse(communeString.get());
        } catch (CommuneParseException e) {
            throw new AdresseParseException(texte, e);
        }
        adresse = Optional
                .ofNullable(new Adresse(numero.get(), voie.get(), lieuDit.get(), commune.get(), bp.get(), cedex.get()));
        return adresse;
    }

}