package com.github.mtesmct.rieau.api.infra.persistence.jpa.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumStatuts;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Statut;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossierNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.domain.services.DateService;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.domain.services.StatutService;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaAdresse;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaCodePieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaMessage;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaNature;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPersonne;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointeId;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaProjet;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaStatut;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaUser;
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
	@Qualifier("instructeurNonBeta")
	private Personne instructeurNonBeta;

	@Autowired
	private JpaPersonneFactory jpaPersonneFactory;
	@Autowired
	private JpaProjetFactory jpaProjetFactory;

	private JpaPersonne jpaPersonne;

	@Autowired
	private DateService dateService;
	@Autowired
	private FichierService fichierService;
	@Autowired
	private FichierFactory fichierFactory;
	@Autowired
	private StatutService statutService;

	@BeforeEach
	public void setUp() throws CommuneNotFoundException, StatutForbiddenException, TypeStatutNotFoundException,
			TypeDossierNotFoundException, FileNotFoundException {
		this.jpaPersonne = new JpaPersonne(this.deposantBeta.identity().toString(), this.deposantBeta.email(), "44100");
		Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01",
				new ParcelleCadastrale("0", "1", "2"), true, true);
		projet.localisation().ajouterParcelle(new ParcelleCadastrale("3", "4", "5"));
		assertEquals(2, projet.localisation().parcellesCadastrales().size());
		File cerfaFile = new File("src/test/fixtures/dummy.pdf");
		Fichier cerfaFichier = this.fichierFactory.creer(cerfaFile, "application/pdf");
		this.fichierService.save(cerfaFichier);
		this.dossier = this.dossierFactory.creer(this.jpaPersonneFactory.fromJpa(this.jpaPersonne), EnumTypes.DPMI,
				projet, cerfaFichier.identity());
		assertFalse(this.dossier.historiqueStatuts().isEmpty());
		assertEquals(1, this.dossier.historiqueStatuts().size());
		assertEquals(this.dossier.deposant().identity().toString(), this.deposantBeta.identity().toString());
		assertEquals(this.dossier.deposant().email(), jpaPersonne.getEmail());
	}

	@Test
	public void saveTest() throws Exception {
        this.statutService.qualifier(this.dossier);
        this.statutService.instruire(this.dossier);
        this.statutService.declarerIncomplet(this.dossier, this.instructeurNonBeta, "Incomplet!");
		this.dossier = this.repository.save(this.dossier);
		assertFalse(this.dossier.historiqueStatuts().isEmpty());
		assertEquals(4, this.dossier.historiqueStatuts().size());
		assertEquals(2, this.dossier.projet().localisation().parcellesCadastrales().size());
		assertEquals(this.dossier.deposant().identity().toString(), this.deposantBeta.identity().toString());
		assertEquals(this.dossier.deposant().email(), this.deposantBeta.email());
		Optional<JpaDossier> optionalJpaDossier = this.entityManager.getEntityManager().unwrap(Session.class)
				.bySimpleNaturalId(JpaDossier.class).loadOptional(this.dossier.identity().toString());
		assertTrue(optionalJpaDossier.isPresent());
		JpaDossier jpaDossier = optionalJpaDossier.get();
		assertEquals(this.dossier.identity().toString(), jpaDossier.getDossierId());
		assertFalse(jpaDossier.getStatuts().isEmpty());
		assertTrue(this.dossier.statutActuel().isPresent());
		assertEquals(4, jpaDossier.getStatuts().size());
		assertEquals(this.dossier.statutActuel().get().type().identity(), EnumStatuts.INCOMPLET);
		assertEquals(this.dossier.type().type(), jpaDossier.getType());
		JpaProjet jpaProjet = this.entityManager.find(JpaProjet.class, jpaDossier.getId());
		assertNotNull(jpaProjet);
		assertEquals(this.dossier.projet().nature().nouvelleConstruction(),
				jpaProjet.getNature().isConstructionNouvelle());
		assertEquals(this.dossier.projet().localisation().lotissement(), jpaProjet.isLotissement());
		assertEquals(this.dossier.projet().localisation().adresse().numero(), jpaProjet.getAdresse().getNumero());
		assertEquals(this.dossier.projet().localisation().adresse().voie(), jpaProjet.getAdresse().getVoie());
		assertEquals(this.dossier.projet().localisation().adresse().lieuDit(), jpaProjet.getAdresse().getLieuDit());
		assertEquals(this.dossier.projet().localisation().adresse().commune().codePostal(),
				jpaProjet.getAdresse().getCodePostal());
		assertEquals(this.dossier.projet().localisation().adresse().bp(), jpaProjet.getAdresse().getBp());
		assertEquals(this.dossier.projet().localisation().adresse().cedex(), jpaProjet.getAdresse().getCedex());
		assertEquals(this.dossier.projet().localisation().parcellesCadastrales().stream()
				.map(ParcelleCadastrale::toFlatString).collect(Collectors.joining(this.jpaProjetFactory.joining())),
				jpaProjet.getParcelles());
		assertFalse(jpaDossier.getMessages().isEmpty());
		assertEquals(1, jpaDossier.getMessages().size());
		JpaMessage jpaMessage = jpaDossier.getMessages().iterator().next();
		assertEquals(this.instructeurNonBeta.identity().toString(), jpaMessage.getAuteur().getId());
		assertEquals(this.instructeurNonBeta.email(), jpaMessage.getAuteur().getEmail());
		assertEquals("Incomplet!", jpaMessage.getContenu());
	}

	@Test
	public void findByIdTest() throws Exception {
		JpaUser user = new JpaUser(this.jpaPersonne.getPersonneId(), this.jpaPersonne.getEmail());
		JpaDossier jpaDossier = new JpaDossier(this.dossier.identity().toString(),
				user, EnumTypes.DPMI);
		jpaDossier.addStatut(new JpaStatut(jpaDossier, EnumStatuts.DEPOSE, this.dateService.now()));
		jpaDossier.addStatut(new JpaStatut(jpaDossier, EnumStatuts.QUALIFIE, this.dateService.now()));
		jpaDossier.addStatut(new JpaStatut(jpaDossier, EnumStatuts.INSTRUCTION, this.dateService.now()));
		JpaStatut jpaStatutActuel = new JpaStatut(jpaDossier, EnumStatuts.INCOMPLET, this.dateService.now());
		jpaDossier.addStatut(jpaStatutActuel);
		String contenu = "Incomplet!";
		jpaDossier.addMessage(new JpaMessage(jpaDossier, user, this.dateService.now(), contenu));
		jpaDossier.addPieceJointe(new JpaPieceJointe(
				new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(EnumTypes.DPMI.toString(), "0"), "1")));
		JpaProjet jpaProjet = new JpaProjet(jpaDossier, new JpaNature(true),
				new JpaAdresse("numero", "voie", "lieuDit", "codePostal", "bp", "cedex"), "1-2-3,4-5-6", true);
		jpaDossier.addProjet(jpaProjet);
		jpaDossier = this.entityManager.persistAndFlush(jpaDossier);
		Optional<Dossier> dossier = this.repository.findById(jpaDossier.getDossierId());
		assertTrue(dossier.isPresent());
		assertEquals(dossier.get().identity().toString(), jpaDossier.getDossierId());
		assertFalse(jpaDossier.getStatuts().isEmpty());
		assertEquals(4, jpaDossier.getStatuts().size());
		assertEquals(jpaStatutActuel.getStatut(), dossier.get().statutActuel().get().type().identity());
		assertEquals(jpaStatutActuel.getDateDebut(), dossier.get().statutActuel().get().dateDebut());
		assertEquals(jpaDossier.getType(), dossier.get().type().type());
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
		assertEquals(jpaProjet.isLotissement(), dossier.get().projet().localisation().lotissement());
		assertFalse(jpaDossier.getMessages().isEmpty());
		assertEquals(1, jpaDossier.getMessages().size());
		JpaMessage jpaMessage = jpaDossier.getMessages().iterator().next();
		assertEquals(contenu, jpaMessage.getContenu());
		assertEquals(user, jpaMessage.getAuteur());
	}

	@Test
	public void findByDeposantTest() throws Exception {
		JpaDossier jpaDossier = new JpaDossier(this.dossier.identity().toString(),
				new JpaUser(this.jpaPersonne.getPersonneId(), this.jpaPersonne.getEmail()), EnumTypes.DPMI);
		jpaDossier.addStatut(new JpaStatut(jpaDossier, EnumStatuts.DEPOSE, this.dateService.now()));
		jpaDossier.addPieceJointe(new JpaPieceJointe(
				new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(EnumTypes.DPMI.toString(), "0"), "0")));
		jpaDossier.addPieceJointe(new JpaPieceJointe(
				new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(EnumTypes.DPMI.toString(), "1"), "1")));
		jpaDossier.addPieceJointe(new JpaPieceJointe(
				new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(EnumTypes.DPMI.toString(), "2"), "2")));
		JpaProjet jpaProjet = new JpaProjet(jpaDossier, new JpaNature(true),
				new JpaAdresse("numero", "voie", "lieuDit", "codePostal", "bp", "cedex"), "1-2-3,4-5-6", true);
		jpaDossier.addProjet(jpaProjet);
		jpaDossier = this.entityManager.persistAndFlush(jpaDossier);
		List<Dossier> dossiers = this.repository.findByDeposantId(this.deposantBeta.identity().toString());
		assertFalse(dossiers.isEmpty());
		assertEquals(1, dossiers.size());
		assertEquals(jpaDossier.getDossierId(), dossiers.get(0).identity().toString());
		assertEquals(jpaDossier.getType(), dossiers.get(0).type().type());
		assertFalse(dossiers.get(0).historiqueStatuts().isEmpty());
		assertEquals(1, dossiers.get(0).historiqueStatuts().size());
		Statut statut = dossiers.get(0).historiqueStatuts().iterator().next();
		JpaStatut jpaStatut = jpaDossier.getStatuts().iterator().next();
		assertEquals(jpaStatut.getStatut(), statut.type().identity());
		assertEquals(jpaStatut.getDateDebut(), jpaStatut.getDateDebut());
	}

	@Test
	public void findByCommuneTest() throws Exception {
		JpaDossier jpaDossier = new JpaDossier(this.dossier.identity().toString(),
				new JpaUser(this.jpaPersonne.getPersonneId(), this.jpaPersonne.getEmail()), EnumTypes.DPMI);
		jpaDossier.addStatut(new JpaStatut(jpaDossier, EnumStatuts.DEPOSE, this.dateService.now()));
		jpaDossier.addPieceJointe(new JpaPieceJointe(
				new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(EnumTypes.DPMI.toString(), "0"), "1")));
		JpaProjet jpaProjet = new JpaProjet(jpaDossier, new JpaNature(true),
				new JpaAdresse("numero", "voie", "lieuDit", "codePostal", "bp", "cedex"), "1-2-3,4-5-6", true);
		jpaDossier.addProjet(jpaProjet);
		jpaDossier = this.entityManager.persistAndFlush(jpaDossier);
		List<Dossier> dossiers = this.repository.findByCommune("codePostal");
		assertFalse(dossiers.isEmpty());
		assertEquals(1, dossiers.size());
		Dossier dossier = dossiers.get(0);
		assertEquals(jpaDossier.getDossierId(), dossier.identity().toString());
		assertEquals(jpaDossier.getType(), dossier.type().type());
		assertFalse(jpaDossier.getStatuts().isEmpty());
		assertEquals(1, jpaDossier.getStatuts().size());
		JpaStatut jpaStatut = jpaDossier.getStatuts().iterator().next();
		assertTrue(dossier.statutActuel().isPresent());
		assertEquals(jpaStatut.getStatut(), dossier.statutActuel().get().type().identity());
		assertEquals(jpaStatut.getDateDebut(), dossier.statutActuel().get().dateDebut());
	}

	@Test
	public void findByFichierIdTest() throws Exception {
		JpaDossier jpaDossier = new JpaDossier(this.dossier.identity().toString(),
				new JpaUser(this.jpaPersonne.getPersonneId(), this.jpaPersonne.getEmail()), EnumTypes.DPMI);
		jpaDossier.addStatut(new JpaStatut(jpaDossier, EnumStatuts.DEPOSE, this.dateService.now()));
		jpaDossier.addPieceJointe(new JpaPieceJointe(
				new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(EnumTypes.DPMI.toString(), "0"), "1")));
		JpaProjet jpaProjet = new JpaProjet(jpaDossier, new JpaNature(true),
				new JpaAdresse("numero", "voie", "lieuDit", "codePostal", "bp", "cedex"), "1-2-3,4-5-6", true);
		jpaDossier.addStatut(new JpaStatut(jpaDossier, EnumStatuts.DEPOSE, this.dateService.now()));
		JpaPieceJointe jpaPieceJointe = new JpaPieceJointe(
				new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(EnumTypes.DPMI.toString(), "1"), "fichierId"));
		jpaDossier.addProjet(jpaProjet);
		jpaDossier.addPieceJointe(jpaPieceJointe);
		jpaDossier = this.entityManager.persistAndFlush(jpaDossier);
		Optional<Dossier> dossier = this.repository.findByFichierId("fichierId");
		assertTrue(dossier.isPresent());
		assertEquals(jpaDossier.getDossierId(), dossier.get().identity().toString());
		assertEquals(jpaProjet.getAdresse().getCodePostal(),
				dossier.get().projet().localisation().adresse().commune().codePostal());
		assertEquals(this.jpaPersonne.getPersonneId(), dossier.get().deposant().identity().toString());
	}

	@Test
	public void deleteTest() throws Exception {
		JpaUser user = new JpaUser(this.jpaPersonne.getPersonneId(), this.jpaPersonne.getEmail());
		JpaDossier jpaDossier = new JpaDossier(this.dossier.identity().toString(),
				user, EnumTypes.DPMI);
		jpaDossier.addStatut(new JpaStatut(jpaDossier, EnumStatuts.DEPOSE, this.dateService.now()));
		jpaDossier.addStatut(new JpaStatut(jpaDossier, EnumStatuts.QUALIFIE, this.dateService.now()));
		jpaDossier.addStatut(new JpaStatut(jpaDossier, EnumStatuts.INSTRUCTION, this.dateService.now()));
		JpaStatut jpaStatutActuel = new JpaStatut(jpaDossier, EnumStatuts.INCOMPLET, this.dateService.now());
		jpaDossier.addStatut(jpaStatutActuel);
		String contenu = "Incomplet!";
		jpaDossier.addMessage(new JpaMessage(jpaDossier, user, this.dateService.now(), contenu));
		jpaDossier.addPieceJointe(new JpaPieceJointe(
				new JpaPieceJointeId(jpaDossier, new JpaCodePieceJointe(EnumTypes.DPMI.toString(), "0"), "1")));
		JpaProjet jpaProjet = new JpaProjet(jpaDossier, new JpaNature(true),
		new JpaAdresse("numero", "voie", "lieuDit", "codePostal", "bp", "cedex"), "1-2-3,4-5-6", true);
		jpaDossier.addProjet(jpaProjet);
		jpaDossier = this.entityManager.persistAndFlush(jpaDossier);
		this.repository.delete(this.dossier.identity());
		assertNull(this.entityManager.find(JpaDossier.class, jpaDossier.getId()));
	}


}