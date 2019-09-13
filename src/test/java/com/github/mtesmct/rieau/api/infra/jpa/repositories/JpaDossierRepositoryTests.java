package com.github.mtesmct.rieau.api.infra.jpa.repositories;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierFactory;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.DateService;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPersonnePhysique;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaPersonnePhysiqueFactory;

import org.hibernate.Session;
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
public class JpaDossierRepositoryTests {

    @Autowired
	private TestEntityManager entityManager;

	@Autowired
	private DossierRepository repository;

    @Autowired
    @Qualifier("dateTimeConverter")
	private DateConverter dateConverter;

    @Autowired
	private DateService dateService;
    @Autowired
    private DossierFactory dossierFactory;
	
	private Dossier dossier;
    @Autowired
    @Qualifier("deposantBeta")
    private PersonnePhysique deposantBeta;

	@Autowired
	private JpaPersonnePhysiqueFactory jpaPersonnePhysiqueFactory;

	@Before
	public void setUp(){
		JpaPersonnePhysique jpaPersonnePhysique = JpaPersonnePhysique.builder().personnePhysiqueId(this.deposantBeta.identity().toString()).email(this.deposantBeta.email()).build();
		jpaPersonnePhysique = this.entityManager.persistAndFlush(jpaPersonnePhysique);
		this.dossier = this.dossierFactory.creer(this.jpaPersonnePhysiqueFactory.fromJpa(jpaPersonnePhysique), null, TypeDossier.DP);
		assertThat(this.dossier.deposant().identity().toString(), is(equalTo(this.deposantBeta.identity().toString())));
		assertThat(this.dossier.deposant().email(), is(equalTo(jpaPersonnePhysique.getEmail())));
    }
    
	@Test
	public void saveTest() throws Exception {
        this.dossier = this.repository.save(this.dossier);
		assertThat(this.dossier.deposant().identity().toString(), is(equalTo(this.deposantBeta.identity().toString())));
		assertThat(this.dossier.deposant().email(), is(equalTo(this.deposantBeta.email())));
		Optional<JpaDossier> optionalJpaDossier = this.entityManager.getEntityManager().unwrap(Session.class)
		.bySimpleNaturalId(JpaDossier.class)
		.loadOptional(this.dossier.identity().toString());
		assertThat(optionalJpaDossier.isPresent(), is(true));
		JpaDossier jpaDossier = optionalJpaDossier.get();
		assertThat(jpaDossier.getDossierId(), is(equalTo(this.dossier.identity().toString())));
		assertThat(jpaDossier.getStatut(), is(equalTo(this.dossier.statut())));
		assertThat(jpaDossier.getType(), is(equalTo(this.dossier.type())));
		assertThat(jpaDossier.getDate(), is(equalTo(this.dossier.dateDepot())));
    }
    
    @Test
	public void findByDeposantAndByIdTest() throws Exception {
		JpaDossier jpaDossier = JpaDossier.builder().dossierId(this.dossier.identity().toString()).statut(StatutDossier.DEPOSE).date(new Date(this.dateService.now().getTime())).deposantId(this.deposantBeta.identity().toString()).deposantEmail(this.deposantBeta.email()).type(TypeDossier.DP).build();
		jpaDossier = this.entityManager.persistAndFlush(jpaDossier);
		Optional<Dossier> dossier = this.repository.findByDeposantIdAndId(this.deposantBeta.identity().toString(), jpaDossier.getDossierId());
		assertThat(dossier.isPresent(), is(true));
		assertThat(dossier.get().identity().toString(), is(equalTo(jpaDossier.getDossierId())));
		assertThat(dossier.get().statut(), is(equalTo(jpaDossier.getStatut())));
		assertThat(dossier.get().type(), is(equalTo(jpaDossier.getType())));
		assertThat(dossier.get().dateDepot(), is(equalTo(jpaDossier.getDate())));
	}

    
    @Test
	public void findByDeposantTest() throws Exception {
		JpaDossier jpaDossier = JpaDossier.builder().dossierId(this.dossier.identity().toString()).statut(StatutDossier.DEPOSE).date(new Date(this.dateService.now().getTime())).deposantId(this.deposantBeta.identity().toString()).deposantEmail(this.deposantBeta.email()).type(TypeDossier.DP).build();
		jpaDossier = this.entityManager.persistAndFlush(jpaDossier);
		List<Dossier> dossiers = this.repository.findByDeposantId(this.deposantBeta.identity().toString());
		assertThat(dossiers, not(empty()));
		assertThat(dossiers, hasSize(1));
		assertThat(dossiers.get(0).identity().toString(), is(equalTo(jpaDossier.getDossierId())));
		assertThat(dossiers.get(0).statut(), is(equalTo(jpaDossier.getStatut())));
		assertThat(dossiers.get(0).type(), is(equalTo(jpaDossier.getType())));
		assertThat(dossiers.get(0).dateDepot(), is(equalTo(jpaDossier.getDate())));
	}
    
}