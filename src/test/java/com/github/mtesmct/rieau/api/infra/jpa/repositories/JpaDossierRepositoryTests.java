package com.github.mtesmct.rieau.api.infra.jpa.repositories;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.CodePieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.DossierIdService;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypePieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Naissance;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysique;
import com.github.mtesmct.rieau.api.domain.entities.personnes.PersonnePhysiqueId;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Sexe;
import com.github.mtesmct.rieau.api.domain.repositories.DateRepository;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.infra.date.MockDateRepository;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPersonnePhysique;

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

	private DateRepository dateRepository;
    @Autowired
    private DossierIdService dossierIdService;
	
	private Dossier dossier;

	private String depositaire = "jean.martin";

	@Before
	public void setUp(){
		this.dateRepository = new MockDateRepository(this.dateConverter,"01/01/2019 00:00:00");
		PersonnePhysique demandeur = new PersonnePhysique(new PersonnePhysiqueId(this.depositaire), "jean.martin@monfai.fr", "Martin", "Jean", Sexe.HOMME, new Naissance(this.dateConverter.parse("01/01/1900 00:00:00"), "44000"));
		this.entityManager.persistAndFlush(JpaPersonnePhysique.builder().personnePhysiqueId(this.depositaire).email("jean.martin@monfai.fr").build());
		PieceJointe cerfa = new PieceJointe(new CodePieceJointe(TypePieceJointe.CERFA, null), new File("test"));
		this.dossier = new Dossier(this.dossierIdService.creer(), demandeur);
    }

    
    @Test
	public void saveTest() throws Exception {
        this.repository.save(this.dossier);
        Optional<JpaDossier> optionalJpaDossier = this.entityManager.getEntityManager().unwrap(Session.class)
		.bySimpleNaturalId(JpaDossier.class)
		.loadOptional(this.dossier.identity().toString());
		assertThat(optionalJpaDossier.isPresent(), is(true));
		JpaDossier jpaDossier = optionalJpaDossier.get();
		assertThat(jpaDossier.getId(), is(equalTo(this.dossier.identity())));
		assertThat(jpaDossier.getStatut(), is(equalTo(this.dossier.statut())));
		assertThat(jpaDossier.getDate(), is(equalTo(this.dossier.dateDepot())));
    }
    
    @Test
	public void findByDemandeurAndByIdTest() throws Exception {
		JpaDossier jpaDossier = JpaDossier.builder().dossierId(this.dossier.identity().toString()).statut(StatutDossier.INSTRUCTION).date(new Date(this.dateRepository.now().getTime())).build();
		this.entityManager.persistAndFlush(jpaDossier);
		Optional<Dossier> dossier = this.repository.findByDemandeurAndId(this.depositaire, jpaDossier.getDossierId());
		assertThat(dossier.isPresent(), is(true));
		assertThat(dossier.get().identity(), is(equalTo(jpaDossier.getDossierId())));
		assertThat(dossier.get().statut(), is(equalTo(jpaDossier.getStatut())));
		assertThat(dossier.get().dateDepot(), is(equalTo(jpaDossier.getDate())));
	}

    
    @Test
	public void findByDemandeurTest() throws Exception {
		JpaDossier jpaDossier = JpaDossier.builder().dossierId(this.dossier.identity().toString()).statut(StatutDossier.INSTRUCTION).date(new Date(this.dateRepository.now().getTime())).build();
		this.entityManager.persist(jpaDossier);
		List<Dossier> dossiers = this.repository.findByDemandeur(this.depositaire);
		assertThat(dossiers, not(empty()));
		assertThat(dossiers, hasSize(1));
		assertThat(dossiers.get(0).identity(), is(equalTo(jpaDossier.getDossierId())));
		assertThat(dossiers.get(0).statut(), is(equalTo(jpaDossier.getStatut())));
		assertThat(dossiers.get(0).dateDepot(), is(equalTo(jpaDossier.getDate())));
	}
    
}