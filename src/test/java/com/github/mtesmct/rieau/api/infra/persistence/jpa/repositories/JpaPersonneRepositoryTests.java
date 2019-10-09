package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.repositories.PersonneRepository;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPersonne;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class JpaPersonneRepositoryTests {

    @Autowired
	private TestEntityManager entityManager;

	@Autowired
	private PersonneRepository personneRepository;

    @Autowired
    @Qualifier("dateTimeConverter")
	private DateConverter dateConverter;
    
    @Test
	public void findByPersonneIdTest() throws Exception {
		JpaPersonne jpaPersonne = new JpaPersonne("insee_01", "toto@tata.fr", "44100");
		this.entityManager.persistAndFlush(jpaPersonne);
		Optional<Personne> personne = this.personneRepository.findByPersonneId(jpaPersonne.getPersonneId());
		assertTrue(personne.isPresent());
		assertEquals(personne.get().email(), jpaPersonne.getEmail());
	}
    
}