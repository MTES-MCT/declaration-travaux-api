package com.github.mtesmct.rieau.api.depositaire.infra.jpa.repositories;

import static org.junit.Assert.assertThat;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Identite;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.IdentiteRepository;
import com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.entities.JpaIdentite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class JpaIdentiteRepositoryTest {

    @Autowired
	private TestEntityManager entityManager;

	@Autowired
    private IdentiteRepository repository;
    
    @Test
	public void saveTest() throws Exception {
        this.repository.save(new Identite("test", "Martin", "Jean", "test@monfai.fr"));
        JpaIdentite jpaIdentite = this.entityManager.find(JpaIdentite.class, "test");
		assertThat(jpaIdentite, notNullValue());
		assertThat(jpaIdentite.getId(), is(equalTo("test")));
		assertThat(jpaIdentite.getEmail(), is(equalTo("test@monfai.fr")));
    }
    
    @Test
	public void findByIdTest() throws Exception {
		this.entityManager.persistAndFlush(JpaIdentite.builder().id("test").nom("Martin").prenom("Jean").email("test@monfai.fr").build());
		Optional<Identite> identite = this.repository.findById("test");
		assertThat(identite.isPresent(), is(true));
		assertThat(identite.get().getId(), is(equalTo("test")));
		assertThat(identite.get().getEmail(), is(equalTo("test@monfai.fr")));
	}

    
}