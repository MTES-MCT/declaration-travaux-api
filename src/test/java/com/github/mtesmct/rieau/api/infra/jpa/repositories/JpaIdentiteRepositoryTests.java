package com.github.mtesmct.rieau.api.infra.jpa.repositories;

import static org.junit.Assert.assertThat;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

import com.github.mtesmct.rieau.api.domain.entities.Identite;
import com.github.mtesmct.rieau.api.domain.repositories.IdentiteRepository;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaIdentite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class JpaIdentiteRepositoryTests {

    @Autowired
	private TestEntityManager entityManager;

	@Autowired
    private IdentiteRepository repository;
    
    @Test
	public void saveTest() throws Exception {
        this.repository.save(new Identite("jean.martin", "Martin", "Jean", "jean.martin@monfai.fr"));
        JpaIdentite jpaIdentite = this.entityManager.find(JpaIdentite.class, "jean.martin");
		assertThat(jpaIdentite, notNullValue());
		assertThat(jpaIdentite.getId(), is(equalTo("jean.martin")));
		assertThat(jpaIdentite.getEmail(), is(equalTo("jean.martin@monfai.fr")));
    }
    
    @Test
	public void findByIdTest() throws Exception {
		this.entityManager.persist(JpaIdentite.builder().id("jean.martin").nom("Martin").prenom("Jean").email("jean.martin@monfai.fr").build());
		Optional<Identite> identite = this.repository.findById("jean.martin");
		assertThat(identite.isPresent(), is(true));
		assertThat(identite.get().getId(), is(equalTo("jean.martin")));
		assertThat(identite.get().getEmail(), is(equalTo("jean.martin@monfai.fr")));
	}

    
}