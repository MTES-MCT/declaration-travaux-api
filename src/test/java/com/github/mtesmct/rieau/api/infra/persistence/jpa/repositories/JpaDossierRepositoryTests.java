package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutDossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.domain.services.DateService;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaAdresse;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaCodePieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDeposant;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaNature;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPersonne;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointeId;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaProjet;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaPersonneFactory;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaProjetFactory;

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
	@Autowired
	private ProjetFactory projetFactory;

	private Dossier dossier;
	@Autowired
	@Qualifier("deposantBeta")
	private Personne deposantBeta;
	@Autowired
	@Qualifier("autreDeposantBeta")
	private Personne autreDeposantBeta;

	@Autowired
	private JpaPersonneFactory jpaPersonneFactory;
    @Autowired
    private JpaProjetFactory jpaProjetFactory;

	private JpaPersonne jpaPersonne;

	@BeforeEach
	public void setUp() throws CommuneNotFoundException {
		this.jpaPersonne = new JpaPersonne(this.deposantBeta.identity().toString(), this.deposantBeta.email());
		Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01", new ParcelleCadastrale("0","1","2"), true);
		projet.localisation().ajouterParcelle(new ParcelleCadastrale("3","4","5"));
		assertEquals(2, projet.localisation().parcellesCadastrales().size());
		this.dossier = this.dossierFactory.creer(this.jpaPersonneFactory.fromJpa(this.jpaPersonne), TypesDossier.DP,
				projet);
		assertEquals(this.dossier.deposant().identity().toString(), this.deposantBeta.identity().toString());
		assertEquals(this.dossier.deposant().email(), jpaPersonne.getEmail());
	}

	@Test
	public void saveTest() throws Exception {
		this.dossier = this.repository.save(this.dossier);
		assertEquals(2, this.dossier.projet().localisation().parcellesCadastrales().size());
		assertEquals(this.dossier.deposant().identity().toString(), this.deposantBeta.identity().toString());
		assertEquals(this.dossier.deposant().email(), this.deposantBeta.email());
		Optional<JpaDossier> optionalJpaDossier = this.entityManager.getEntityManager().unwrap(Session.class)
				.bySimpleNaturalId(JpaDossier.class).loadOptional(this.dossier.identity().toString());
		assertTrue(optionalJpaDossier.isPresent());
		JpaDossier jpaDossier = optionalJpaDossier.get();
		assertEquals(this.dossier.identity().toString(), jpaDossier.getDossierId());
		assertEquals(this.dossier.statut(), jpaDossier.getStatut());
		assertEquals(this.dossier.type().type(), jpaDossier.getType());
		assertEquals(this.dossier.dateDepot(), jpaDossier.getDateDepot());
		JpaProjet jpaProjet = this.entityManager.find(JpaProjet.class, jpaDossier.getId());
		assertNotNull(jpaProjet);
		assertEquals(this.dossier.projet().nature().nouvelleConstruction(), jpaProjet.getNature().isConstructionNouvelle());
		assertEquals(this.dossier.projet().localisation().adresse().numero(), jpaProjet.getAdresse().getNumero());
		assertEquals(this.dossier.projet().localisation().adresse().voie(), jpaProjet.getAdresse().getVoie());
		assertEquals(this.dossier.projet().localisation().adresse().lieuDit(), jpaProjet.getAdresse().getLieuDit());
		assertEquals(this.dossier.projet().localisation().adresse().commune().codePostal(), jpaProjet.getAdresse().getCodePostal());
		assertEquals(this.dossier.projet().localisation().adresse().bp(), jpaProjet.getAdresse().getBp());
		assertEquals(this.dossier.projet().localisation().adresse().cedex(), jpaProjet.getAdresse().getCedex());
		assertEquals(this.dossier.projet().localisation().parcellesCadastrales().stream().map(ParcelleCadastrale::toFlatString).collect(Collectors.joining(this.jpaProjetFactory.joining())), jpaProjet.getParcelles());
	}

	@Test
	public void findByIdTest() throws Exception {
		JpaDossier jpaDossier = new JpaDossier(this.dossier.identity().toString(), StatutDossier.DEPOSE,
				new Date(this.dateService.now().getTime()),
				new JpaDeposant(this.jpaPersonne.getPersonneId(), this.jpaPersonne.getEmail()), TypesDossier.DP);
		jpaDossier = this.entityManager.persistAndFlush(jpaDossier);
		JpaProjet jpaProjet = new JpaProjet(jpaDossier, new JpaNature(true),
				new JpaAdresse("numero", "voie", "lieuDit", "codePostal", "bp", "cedex"), "1-2-3,4-5-6");
		jpaProjet = this.entityManager.persistAndFlush(jpaProjet);
		Optional<Dossier> dossier = this.repository.findById(jpaDossier.getDossierId());
		assertTrue(dossier.isPresent());
		assertEquals(jpaDossier.getDossierId(), dossier.get().identity().toString());
		assertEquals(jpaDossier.getStatut(), dossier.get().statut());
		assertEquals(jpaDossier.getType(), dossier.get().type().type());
		assertEquals(jpaDossier.getDateDepot(), dossier.get().dateDepot());
		assertEquals(jpaDossier.getDateDepot(), dossier.get().dateDepot());
		assertEquals(jpaProjet.getNature().isConstructionNouvelle(),
				dossier.get().projet().nature().nouvelleConstruction());
		assertEquals(jpaProjet.getAdresse().getNumero(), dossier.get().projet().localisation().adresse().numero());
		assertEquals(jpaProjet.getAdresse().getVoie(), dossier.get().projet().localisation().adresse().voie());
		assertEquals(jpaProjet.getAdresse().getLieuDit(), dossier.get().projet().localisation().adresse().lieuDit());
		assertEquals(jpaProjet.getAdresse().getCodePostal(),
				dossier.get().projet().localisation().adresse().commune().codePostal());
		assertEquals(jpaProjet.getAdresse().getBp(), dossier.get().projet().localisation().adresse().bp());
		assertEquals(jpaProjet.getAdresse().getCedex(), dossier.get().projet().localisation().adresse().cedex());
		assertEquals("1-2-3", dossier.get().projet().localisation().parcellesCadastrales().get(0).toFlatString());
		assertEquals("4-5-6", dossier.get().projet().localisation().parcellesCadastrales().get(1).toFlatString());
	}

	@Test
	public void findByDeposantTest() throws Exception {
		JpaDossier jpaDossier = new JpaDossier(this.dossier.identity().toString(), StatutDossier.DEPOSE,
				new Date(this.dateService.now().getTime()),
				new JpaDeposant(this.jpaPersonne.getPersonneId(), this.jpaPersonne.getEmail()), TypesDossier.DP);
		jpaDossier = this.entityManager.persistAndFlush(jpaDossier);
		JpaProjet jpaProjet = new JpaProjet(jpaDossier, new JpaNature(true),
				new JpaAdresse("numero", "voie", "lieuDit", "codePostal", "bp", "cedex"), "1-2-3,4-5-6");
		jpaProjet = this.entityManager.persistAndFlush(jpaProjet);
		List<Dossier> dossiers = this.repository.findByDeposantId(this.deposantBeta.identity().toString());
		assertFalse(dossiers.isEmpty());
		assertEquals(dossiers.size(), 1);
		assertEquals(dossiers.get(0).identity().toString(), jpaDossier.getDossierId());
		assertEquals(dossiers.get(0).statut(), jpaDossier.getStatut());
		assertEquals(dossiers.get(0).type().type(), jpaDossier.getType());
		assertEquals(dossiers.get(0).dateDepot(), jpaDossier.getDateDepot());
	}

	@Test
	public void isDeposantOwnerTest() throws Exception {
		JpaDossier jpaDossier = new JpaDossier(this.dossier.identity().toString(), StatutDossier.DEPOSE,
				new Date(this.dateService.now().getTime()),
				new JpaDeposant(this.jpaPersonne.getPersonneId(), this.jpaPersonne.getEmail()), TypesDossier.DP);
		String fichierId = "fichier-1";
		JpaPieceJointe pieceJointe = new JpaPieceJointe(
				new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(TypesDossier.DP.toString(), "1"), fichierId));
		jpaDossier.addPieceJointe(pieceJointe);
		jpaDossier = this.entityManager.persistAndFlush(jpaDossier);
		assertTrue(this.repository.isDeposantOwner(this.deposantBeta.identity().toString(), fichierId));
	}

	@Test
	public void isDeposantNotOwnerTest() throws Exception {
		JpaDossier jpaDossier = new JpaDossier(this.dossier.identity().toString(), StatutDossier.DEPOSE,
				new Date(this.dateService.now().getTime()),
				new JpaDeposant(this.jpaPersonne.getPersonneId(), this.jpaPersonne.getEmail()), TypesDossier.DP);
		String fichierId = "fichier-1";
		JpaPieceJointe pieceJointe = new JpaPieceJointe(
				new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(TypesDossier.DP.toString(), "1"), fichierId));
		jpaDossier.addPieceJointe(pieceJointe);
		jpaDossier = this.entityManager.persistAndFlush(jpaDossier);
		assertFalse(this.repository.isDeposantOwner(this.autreDeposantBeta.identity().toString(), fichierId));
	}

}