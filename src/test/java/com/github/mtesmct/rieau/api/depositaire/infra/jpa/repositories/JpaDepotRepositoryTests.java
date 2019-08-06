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

import com.github.mtesmct.rieau.api.depositaire.domain.entities.Depot;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DateRepository;
import com.github.mtesmct.rieau.api.depositaire.domain.repositories.DepotRepository;
import com.github.mtesmct.rieau.api.depositaire.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.depositaire.infra.date.MockDateRepository;
import com.github.mtesmct.rieau.api.depositaire.infra.persistence.jpa.entities.JpaDepot;

import org.junit.Before;
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
public class JpaDepotRepositoryTests {

    @Autowired
	private TestEntityManager entityManager;

	@Autowired
	private DepotRepository repository;
	
    @Autowired
    @Qualifier("dateTimeConverter")
	private DateConverter converter;

	private DateRepository dateRepository;
	
	private Depot depot;

	@Before
	public void setUp(){
		this.dateRepository = new MockDateRepository(this.converter,"01/01/2019 00:00:00");
		this.depot = new Depot("0", "dp", "instruction", this.dateRepository.now());
	}

    
    @Test
	public void saveTest() throws Exception {
        this.repository.save(this.depot);
        JpaDepot jpaDepot = this.entityManager.find(JpaDepot.class, this.depot.getId());
		assertThat(jpaDepot, notNullValue());
		assertThat(jpaDepot.getId(), is(equalTo(this.depot.getId())));
		assertThat(jpaDepot.getType(), is(equalTo(this.depot.getType())));
		assertThat(jpaDepot.getEtat(), is(equalTo(this.depot.getEtat())));
		assertThat(jpaDepot.getDate(), is(equalTo(this.depot.getDate())));
    }
    
    @Test
	public void findByIdTest() throws Exception {
		JpaDepot jpaDepot = JpaDepot.builder().id("0").type("dp").etat("instruction").date(new Date(this.dateRepository.now().getTime())).build();
		this.entityManager.persistAndFlush(jpaDepot);
		Optional<Depot> depot = this.repository.findById(jpaDepot.getId());
		assertThat(depot.isPresent(), is(true));
		assertThat(depot.get().getId(), is(equalTo(jpaDepot.getId())));
		assertThat(depot.get().getType(), is(equalTo(jpaDepot.getType())));
		assertThat(depot.get().getEtat(), is(equalTo(jpaDepot.getEtat())));
		assertThat(depot.get().getDate(), is(equalTo(jpaDepot.getDate())));
	}

    
    @Test
	public void findAllTest() throws Exception {
		JpaDepot jpaDepot = JpaDepot.builder().id("0").type("dp").etat("instruction").date(new Date(this.dateRepository.now().getTime())).build();
		this.entityManager.persist(jpaDepot);
		List<Depot> depots = this.repository.findAll();
		assertThat(depots, not(empty()));
		assertThat(depots, hasSize(1));
		assertThat(depots.get(0).getId(), is(equalTo(jpaDepot.getId())));
		assertThat(depots.get(0).getType(), is(equalTo(jpaDepot.getType())));
		assertThat(depots.get(0).getEtat(), is(equalTo(jpaDepot.getEtat())));
		assertThat(depots.get(0).getDate(), is(equalTo(jpaDepot.getDate())));
	}
    
}