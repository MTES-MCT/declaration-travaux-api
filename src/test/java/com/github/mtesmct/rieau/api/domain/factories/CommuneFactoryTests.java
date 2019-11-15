package com.github.mtesmct.rieau.api.domain.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Commune;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CommuneFactoryTests {
    @Autowired
    private CommuneFactory communeFactory;

    @Test
    public void parseOk() throws CommuneParseException {
        Optional<Commune> commune = this.communeFactory
                .parse("Commune={ codePostal={codePostal}, nom={nom}, departement={departement} }");
        assertTrue(commune.isPresent());
        assertEquals("codePostal", commune.get().codePostal());
        assertEquals("codePostal", commune.get().nom());
        assertEquals("co", commune.get().departement());
    }

    @Test
    public void parseKOPasCommune() {
        assertThrows(CommuneParseException.class, () -> this.communeFactory.parse("toto={id={0}}"));
    }
}