package com.github.mtesmct.rieau.api.depositaire.infra.jpa.repositories;

import static org.junit.Assert.assertThat;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNot.not;

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Demande;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DateRepository;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DemandeRepository;
import com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.entities.JpaDemande;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class JpaDemandeRepositoryIntegrationTest {

    @Autowired
	private TestEntityManager entityManager;

	@Autowired
    private DemandeRepository repository;
	@Autowired
	private DateRepository dateRepository;
	
	private Demande demande;

	@Before
	public void setUp(){
		this.demande = new Demande("0", "dp", "instruction", this.dateRepository.now());
	}

    
    @Test
	public void saveTest() throws Exception {
        this.repository.save(this.demande);
        JpaDemande jpaDemande = this.entityManager.find(JpaDemande.class, this.demande.getId());
		assertThat(jpaDemande, notNullValue());
		assertThat(jpaDemande.getId(), is(equalTo(this.demande.getId())));
		assertThat(jpaDemande.getType(), is(equalTo(this.demande.getType())));
		assertThat(jpaDemande.getEtat(), is(equalTo(this.demande.getEtat())));
		assertThat(jpaDemande.getDate(), is(equalTo(this.demande.getDate())));
    }
    
    @Test
	public void findByIdTest() throws Exception {
		JpaDemande jpaDemande = JpaDemande.builder().id("0").type("dp").etat("instruction").date(new Date(this.dateRepository.now().getTime())).build();
		this.entityManager.persist(jpaDemande);
		Optional<Demande> demande = this.repository.findById(jpaDemande.getId());
		assertThat(demande.isPresent(), is(true));
		assertThat(demande.get().getId(), is(equalTo(jpaDemande.getId())));
		assertThat(demande.get().getType(), is(equalTo(jpaDemande.getType())));
		assertThat(demande.get().getEtat(), is(equalTo(jpaDemande.getEtat())));
		assertThat(demande.get().getDate(), is(equalTo(jpaDemande.getDate())));
	}

    
    @Test
	public void findAllTest() throws Exception {
		JpaDemande jpaDemande = JpaDemande.builder().id("0").type("dp").etat("instruction").date(new Date(this.dateRepository.now().getTime())).build();
		this.entityManager.persist(jpaDemande);
		List<Demande> demandes = this.repository.findAll();
		assertThat(demandes, not(empty()));
		assertThat(demandes, hasSize(1));
		assertThat(demandes.get(0).getId(), is(equalTo(jpaDemande.getId())));
		assertThat(demandes.get(0).getType(), is(equalTo(jpaDemande.getType())));
		assertThat(demandes.get(0).getEtat(), is(equalTo(jpaDemande.getEtat())));
		assertThat(demandes.get(0).getDate(), is(equalTo(jpaDemande.getDate())));
	}
    
}