package com.github.mtesmct.rieau.api.infra.jpa.repositories;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;
import com.github.mtesmct.rieau.api.domain.repositories.PersonnePhysiqueRepository;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPersonnePhysique;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class JpaPersonnePhysiqueRepositoryTests {

    @Autowired
	private TestEntityManager entityManager;

	@Autowired
	private PersonnePhysiqueRepository personnePhysiqueRepository;

    @Autowired
    @Qualifier("dateTimeConverter")
	private DateConverter dateConverter;
    
    @Test
	public void findByEmailTest() throws Exception {
		JpaPersonnePhysique jpaPersonnePhysique = JpaPersonnePhysique.builder().personnePhysiqueId("insee_01").email("email@email.fr").build();
		this.entityManager.persistAndFlush(jpaPersonnePhysique);
		Optional<PersonnePhysique> personnePhysique = this.personnePhysiqueRepository.findByEmail(jpaPersonnePhysique.getEmail());
		assertThat(personnePhysique.isPresent(), is(true));
		assertThat(personnePhysique.get().email(), is(equalTo(jpaPersonnePhysique.getEmail())));
	}
    
}