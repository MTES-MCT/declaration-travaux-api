package com.github.mtesmct.rieau.api.infra.communes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Commune;
import com.github.mtesmct.rieau.api.domain.services.CommuneService;
import com.github.mtesmct.rieau.api.infra.config.AppProperties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ApiCommuneServiceIT {

    @Autowired
    private CommuneService communeService;
    @Autowired
    private AppProperties properties;

    @Test
	public void findByCodeCodePostalTest() throws Exception {
		Optional<Commune> commune = this.communeService.findByCodeCodePostal("44100");
		assertFalse(commune.isEmpty());
		assertEquals("44100", commune.get().codePostal());
		assertEquals(this.properties.getCommunesUrl() == "" ? "Nantes" : "44100", commune.get().nom());
		assertEquals("44", commune.get().department());
	}
    
}