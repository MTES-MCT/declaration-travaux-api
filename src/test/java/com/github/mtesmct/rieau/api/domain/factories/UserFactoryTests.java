package com.github.mtesmct.rieau.api.domain.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.personnes.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserFactoryTests {
    @Autowired
    private UserFactory userFactory;

    @Test
    public void parseUserOk() throws UserParseException {
        Optional<User> user = this.userFactory.parse(
                "User={identite={Personne={id={0}, prenom={prenom}, nom={nom}, sexe={HOMME}, email={toto@toto.fr}, adresse={Adresse={ numero={numero}, voie={voie}, lieuDit={lieuDit}, commune={Commune={codePostal={codePostal}, nom={nom}, departement={departement}}}, bp={bp}, cedex={cedex} }}}, profils={DEPOSANT,BETA} }");
        assertTrue(user.isPresent());
        assertEquals("0", user.get().identity().toString());
        assertEquals("prenom", user.get().identite().prenom());
        assertEquals("nom", user.get().identite().nom());
        assertEquals("HOMME", user.get().identite().sexe().toString());
        assertEquals("toto@toto.fr", user.get().identite().email());
        assertEquals("numero", user.get().identite().adresse().numero());
        assertEquals("voie", user.get().identite().adresse().voie());
        assertEquals("lieuDit", user.get().identite().adresse().lieuDit());
        assertEquals("codePostal", user.get().identite().adresse().commune().codePostal());
        assertEquals("codePostal", user.get().identite().adresse().commune().nom());
        assertEquals("co", user.get().identite().adresse().commune().departement());
        assertEquals("bp", user.get().identite().adresse().bp());
        assertEquals("cedex", user.get().identite().adresse().cedex());
        assertEquals("DEPOSANT,BETA", String.join(",",user.get().profils()));
    }
    @Test
    public void parseUserDeposantBetaOk() throws UserParseException {
        Optional<User> user = this.userFactory.parse(
                "User={ identite={Personne={ id={jean.martin}, prenom={Jean}, nom={Martin}, sexe={null}, email={jean.martin@monfai.fr}, adresse={null} }}, profils={DEPOSANT,BETA} }");
        assertTrue(user.isPresent());
        assertEquals("jean.martin", user.get().identity().toString());
        assertEquals("Jean", user.get().identite().prenom());
        assertEquals("Martin", user.get().identite().nom());
        assertNull(user.get().identite().sexe());
        assertNull(user.get().identite().email());
        assertNull(user.get().identite().adresse());
        assertEquals("DEPOSANT,BETA", String.join(",",user.get().profils()));
    }

    @Test
    public void parseKOPasUser() {
        assertThrows(UserParseException.class,
                () -> this.userFactory.parse("toto={id={0}}"));
    }
}