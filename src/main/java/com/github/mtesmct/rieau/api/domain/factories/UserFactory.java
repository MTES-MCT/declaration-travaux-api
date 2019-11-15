package com.github.mtesmct.rieau.api.domain.factories;

import java.util.Objects;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.Factory;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Adresse;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Commune;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Sexe;
import com.github.mtesmct.rieau.api.domain.entities.personnes.User;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.domain.services.CommuneService;
import com.github.mtesmct.rieau.api.domain.services.StringExtractService;

@Factory
public class UserFactory {

    private StringExtractService stringExtractService;
    private CommuneService communeService;
    private AdresseFactory adresseFactory;

    public UserFactory(StringExtractService stringExtractService, CommuneService communeService,
            AdresseFactory adresseFactory) {
        if (stringExtractService == null)
            throw new NullPointerException("Le service des string extract ne peut pas être nul.");
        this.stringExtractService = stringExtractService;
        if (communeService == null)
            throw new NullPointerException("Le service des communes ne peut pas être nul.");
        this.communeService = communeService;
        if (adresseFactory == null)
            throw new NullPointerException("La factory des adresses ne peut pas être nulle.");
        this.adresseFactory = adresseFactory;
    }

    private Personne creer(String personneId, String email, Sexe sexe, String nom, String prenom, String codePostal,
            String numero, String voie, String lieuDit, String bp, String cedex) throws CommuneNotFoundException {
        if (personneId == null)
            throw new NullPointerException("L'id de la personne ne peut pas être nul.");
        Adresse adresse = null;
        if (codePostal != null) {
            Optional<Commune> commune = this.communeService.findByCodeCodePostal(codePostal);
            if (commune.isEmpty())
                throw new CommuneNotFoundException(codePostal);
            adresse = new Adresse(numero, voie, lieuDit, commune.get(), bp, cedex);
        }
        return new Personne(personneId, nom, prenom, sexe, email, adresse);
    }

    public User creer(String personneId, String email, Sexe sexe, String nom, String prenom, String codePostal,
            String numero, String voie, String lieuDit, String bp, String cedex, String[] profils)
            throws CommuneNotFoundException {
        Personne personne = this.creer(personneId, email, sexe, nom, prenom, codePostal, numero, voie, lieuDit, bp,
                cedex);
        return new User(personne, profils);
    }

    public Optional<User> parse(String texte) throws UserParseException {
        Optional<User> user = Optional.empty();
        // Optional<String> userString = this.stringExtractService.entityExtract("User", texte);
        // if (userString.isEmpty())
        //     throw new UserParseException(texte);
        Optional<String> id = this.stringExtractService.attributeExtract("id", texte);
        Optional<String> email = this.stringExtractService.attributeExtract("email", texte);
        Optional<String> nom = this.stringExtractService.attributeExtract("\\bnom\\b", texte);
        Optional<String> prenom = this.stringExtractService.attributeExtract("prenom", texte);
        Optional<String> sexe = this.stringExtractService.attributeExtract("sexe", texte);
        Optional<String> adresseString = this.stringExtractService.entityExtract("adresse", texte);
        Optional<String> profils = this.stringExtractService.attributeExtract("profils", texte);
        Optional<Adresse> adresse = Optional.empty();
        if (adresseString.isPresent() && !adresseString.get().equals("null")) {
            try {
                adresse = this.adresseFactory.parse(adresseString.get());
            } catch (AdresseParseException e) {
                throw new UserParseException(texte, e);
            }
        }
        Personne personne = null;
        Optional<Sexe> sex = Optional.empty();
        if (sexe.isPresent()) {
            try {
                sex = Optional.ofNullable(Sexe.valueOf(sexe.get()));
            } catch (IllegalArgumentException e) {

            }
        }
        if (sex.isPresent() && email.isPresent() && adresse.isPresent()) {
            personne = new Personne(id.get(), nom.get(), prenom.get(), sex.get(), email.get(), adresse.get());
        } else {
            personne = new Personne(id.get(), nom.get(), prenom.get());

        }
        user = Optional.ofNullable(new User(personne, profils.get().split(",")));
        return user;
    }
}