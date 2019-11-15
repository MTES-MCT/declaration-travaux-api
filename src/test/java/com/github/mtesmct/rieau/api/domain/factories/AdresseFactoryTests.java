package com.github.mtesmct.rieau.api.domain.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Adresse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AdresseFactoryTests {
    @Autowired
    private AdresseFactory adresseFactory;

    @Test
    public void parseOk() throws AdresseParseException {
        Optional<Adresse> adresse = this.adresseFactory.parse(
                "Adresse={ numero={numero}, voie={voie}, lieuDit={lieuDit}, commune={Commune={codePostal={codePostal}, nom={nom}, departement={departement}}}, bp={bp}, cedex={cedex} }");
        assertTrue(adresse.isPresent());
        assertEquals("numero", adresse.get().numero());
        assertEquals("voie", adresse.get().voie());
        assertEquals("lieuDit", adresse.get().lieuDit());
        assertEquals("codePostal", adresse.get().commune().codePostal());
        assertEquals("codePostal", adresse.get().commune().nom());
        assertEquals("co", adresse.get().commune().departement());
        assertEquals("bp", adresse.get().bp());
        assertEquals("cedex", adresse.get().cedex());
    }

    @Test
    public void parseKOPasAdresse() {
    assertThrows(AdresseParseException.class,
                () -> this.adresseFactory.parse("toto={id={0}}"));
    }
}