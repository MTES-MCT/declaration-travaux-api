package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.DateService;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDeposant;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPersonne;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaPersonneFactory;

import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
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
    private Personne deposantBeta;

	@Autowired
	private JpaPersonneFactory jpaPersonneFactory;

	private JpaPersonne jpaPersonne;

	@BeforeEach
	public void setUp(){
		this.jpaPersonne = new JpaPersonne(this.deposantBeta.identity().toString(), this.deposantBeta.email());
		this.jpaPersonne = this.entityManager.persistAndFlush(this.jpaPersonne);
		this.dossier = this.dossierFactory.creer(this.jpaPersonneFactory.fromJpa(this.jpaPersonne), TypesDossier.DP);
		assertEquals(this.dossier.deposant().identity().toString(), this.deposantBeta.identity().toString());
		assertEquals(this.dossier.deposant().email(), jpaPersonne.getEmail());
    }
    
	@Test
	public void saveTest() throws Exception {
        this.dossier = this.repository.save(this.dossier);
		assertEquals(this.dossier.deposant().identity().toString(), this.deposantBeta.identity().toString());
		assertEquals(this.dossier.deposant().email(), this.deposantBeta.email());
		Optional<JpaDossier> optionalJpaDossier = this.entityManager.getEntityManager().unwrap(Session.class)
		.bySimpleNaturalId(JpaDossier.class)
		.loadOptional(this.dossier.identity().toString());
		assertTrue(optionalJpaDossier.isPresent());
		JpaDossier jpaDossier = optionalJpaDossier.get();
		assertEquals(jpaDossier.getDossierId(), this.dossier.identity().toString());
		assertEquals(jpaDossier.getStatut(), this.dossier.statut());
		assertEquals(jpaDossier.getType(), this.dossier.type().type());
		assertEquals(jpaDossier.getDateDepot(), this.dossier.dateDepot());
    }
    
    @Test
	public void findByIdTest() throws Exception {
		JpaDossier jpaDossier = new JpaDossier(this.dossier.identity().toString(), StatutDossier.DEPOSE, new Date(this.dateService.now().getTime()), new JpaDeposant(this.jpaPersonne.getPersonneId(), this.jpaPersonne.getEmail()), TypesDossier.DP);
		jpaDossier = this.entityManager.persistAndFlush(jpaDossier);
		Optional<Dossier> dossier = this.repository.findById(jpaDossier.getDossierId());
		assertTrue(dossier.isPresent());
		assertEquals(dossier.get().identity().toString(), jpaDossier.getDossierId());
		assertEquals(dossier.get().statut(), jpaDossier.getStatut());
		assertEquals(dossier.get().type().type(), jpaDossier.getType());
		assertEquals(dossier.get().dateDepot(), jpaDossier.getDateDepot());
	}

    
    @Test
	public void findByDeposantTest() throws Exception {
		JpaDossier jpaDossier = new JpaDossier(this.dossier.identity().toString(), StatutDossier.DEPOSE, new Date(this.dateService.now().getTime()), new JpaDeposant(this.jpaPersonne.getPersonneId(), this.jpaPersonne.getEmail()), TypesDossier.DP);
		jpaDossier = this.entityManager.persistAndFlush(jpaDossier);
		List<Dossier> dossiers = this.repository.findByDeposantId(this.deposantBeta.identity().toString());
		assertFalse(dossiers.isEmpty());
		assertEquals(dossiers.size(), 1);
		assertEquals(dossiers.get(0).identity().toString(), jpaDossier.getDossierId());
		assertEquals(dossiers.get(0).statut(), jpaDossier.getStatut());
		assertEquals(dossiers.get(0).type().type(), jpaDossier.getType());
		assertEquals(dossiers.get(0).dateDepot(), jpaDossier.getDateDepot());
	}
    
}