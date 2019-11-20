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
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Message;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Statut;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossierNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.User;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.factories.UserFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.domain.services.DateService;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.domain.services.StatutService;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaAdresse;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaCodePieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaDossier;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaMessage;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaNature;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointe;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaPieceJointeId;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaProjet;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaStatut;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.entities.JpaUser;
import com.github.mtesmct.rieau.api.infra.persistence.jpa.factories.JpaProjetFactory;

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
	private JpaSpringDossierRepository jpaSpringDossierRepository;

	@Autowired
	private DossierFactory dossierFactory;
	@Autowired
	private ProjetFactory projetFactory;

	private Dossier dossier;
	@Autowired
	@Qualifier("deposantBeta")
	private User deposantBeta;
	@Autowired
	@Qualifier("autreDeposantBeta")
	private User autreDeposantBeta;
	@Autowired
	@Qualifier("instructeurNonBeta")
	private User instructeurNonBeta;
	@Autowired
	private JpaProjetFactory jpaProjetFactory;

	private JpaUser jpaUser;

	@Autowired
	private DateService dateService;
	@Autowired
	private FichierService fichierService;
	@Autowired
	private FichierFactory fichierFactory;
	@Autowired
	private StatutService statutService;
	@Autowired
	private UserFactory userFactory;

	@BeforeEach
	public void setUp() throws CommuneNotFoundException, StatutForbiddenException, TypeStatutNotFoundException,
			TypeDossierNotFoundException, FileNotFoundException {
		this.jpaUser = new JpaUser(this.deposantBeta.identity().toString(), this.deposantBeta.identite().nom(),
				this.deposantBeta.identite().prenom(), String.join(",", this.deposantBeta.profils()));
		Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01",
				new ParcelleCadastrale("0", "1", "2"), true, true);
		projet.localisation().ajouterParcelle(new ParcelleCadastrale("3", "4", "5"));
		assertEquals(2, projet.localisation().parcellesCadastrales().size());
		File cerfaFile = new File("src/test/fixtures/dummy.pdf");
		Fichier cerfaFichier = this.fichierFactory.creer(cerfaFile, "application/pdf");
		this.fichierService.save(cerfaFichier);
		this.dossier = this.dossierFactory.creer(this.deposantBeta, EnumTypes.DPMI, projet, cerfaFichier.identity());
		this.dossier.ajouterMessage(new Message(this.deposantBeta, this.dateService.now(), "test"));
		assertFalse(this.dossier.historiqueStatuts().isEmpty());
		assertEquals(1, this.dossier.historiqueStatuts().size());
		assertEquals(1, this.dossier.messages().size());
		assertEquals(this.dossier.deposant().identity().toString(), this.deposantBeta.identity().toString());
		assertEquals(this.dossier.deposant().identite().nom(), jpaUser.getNom());
		assertEquals(this.dossier.deposant().identite().prenom(), jpaUser.getPrenom());
		assertEquals(String.join(",", this.dossier.deposant().profils()), jpaUser.getProfils());
	}

	@Test
	public void saveTest() throws Exception {
		this.statutService.qualifier(this.dossier);
		this.statutService.instruire(this.dossier);
		this.statutService.declarerIncomplet(this.dossier, this.instructeurNonBeta, "Incomplet!");
		this.repository.save(this.dossier); //create
		Optional<JpaDossier> optionalJpaDossier = this.jpaSpringDossierRepository.findOneByDossierIdWithPiecesJointes(this.dossier.identity().toString());
		optionalJpaDossier = this.jpaSpringDossierRepository.findOneByDossierIdWithStatuts(this.dossier.identity().toString());
		optionalJpaDossier = this.jpaSpringDossierRepository.findOneByDossierIdWithMessages(this.dossier.identity().toString());
		assertTrue(optionalJpaDossier.isPresent());
		JpaDossier jpaDossier = optionalJpaDossier.get();
		assertEquals(this.dossier.identity().toString(), jpaDossier.getDossierId());
		assertFalse(jpaDossier.getStatuts().isEmpty());
		assertEquals(4, jpaDossier.getStatuts().size());
		JpaStatut jpaStatut = jpaDossier.getStatuts().stream().reduce((first, second) -> second).orElse(null);
		assertEquals(this.dossier.statutActuel().get().type().identity(), jpaStatut.getStatut());
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
		assertEquals(2, jpaDossier.getMessages().size());
		JpaMessage jpaMessage = jpaDossier.getMessages().get(0);
		Optional<User> auteur = this.userFactory.parse(jpaMessage.getAuteur());
		assertTrue(auteur.isPresent());
		assertEquals(this.deposantBeta.identity(), auteur.get().identity());
		assertEquals(this.deposantBeta.identite().nom(), auteur.get().identite().nom());
		assertEquals(this.deposantBeta.identite().prenom(), auteur.get().identite().prenom());
		assertEquals(String.join(",", this.deposantBeta.profils()), String.join(",", auteur.get().profils()));
		assertEquals("test", jpaMessage.getContenu());
		jpaMessage = jpaDossier.getMessages().get(1);
		auteur = this.userFactory.parse(jpaMessage.getAuteur());
		assertTrue(auteur.isPresent());
		assertEquals(this.instructeurNonBeta.identity(), auteur.get().identity());
		assertEquals(this.instructeurNonBeta.identite().nom(), auteur.get().identite().nom());
		assertEquals(this.instructeurNonBeta.identite().prenom(), auteur.get().identite().prenom());
		assertEquals(String.join(",", this.instructeurNonBeta.profils()), String.join(",", auteur.get().profils()));
		assertEquals("Incomplet!", jpaMessage.getContenu());
		this.dossier.ajouterMessage(new Message(this.deposantBeta, this.dateService.now(), "test2"));
		this.repository.save(this.dossier); //update
		optionalJpaDossier = this.jpaSpringDossierRepository.findOneByDossierIdWithPiecesJointes(this.dossier.identity().toString());
		optionalJpaDossier = this.jpaSpringDossierRepository.findOneByDossierIdWithStatuts(this.dossier.identity().toString());
		optionalJpaDossier = this.jpaSpringDossierRepository.findOneByDossierIdWithMessages(this.dossier.identity().toString());
		assertTrue(optionalJpaDossier.isPresent());
		jpaDossier = optionalJpaDossier.get();
		assertFalse(jpaDossier.getMessages().isEmpty());
		assertEquals(3, jpaDossier.getMessages().size());
		jpaMessage = jpaDossier.getMessages().get(2);
		auteur = this.userFactory.parse(jpaMessage.getAuteur());
		assertTrue(auteur.isPresent());
		assertEquals(this.deposantBeta.identity(), auteur.get().identity());
		assertEquals(this.deposantBeta.identite().nom(), auteur.get().identite().nom());
		assertEquals(this.deposantBeta.identite().prenom(), auteur.get().identite().prenom());
		assertEquals(String.join(",", this.deposantBeta.profils()), String.join(",", auteur.get().profils()));
		assertEquals("test2", jpaMessage.getContenu());

	}

	@Test
	public void findByIdTest() throws Exception {
		JpaDossier jpaDossier = new JpaDossier(this.dossier.identity().toString(), this.jpaUser, EnumTypes.DPMI);
		jpaDossier.addStatut(new JpaStatut(jpaDossier, EnumStatuts.DEPOSE, this.dateService.now()));
		jpaDossier.addStatut(new JpaStatut(jpaDossier, EnumStatuts.QUALIFIE, this.dateService.now()));
		jpaDossier.addStatut(new JpaStatut(jpaDossier, EnumStatuts.INSTRUCTION, this.dateService.now()));
		JpaStatut jpaStatutActuel = new JpaStatut(jpaDossier, EnumStatuts.INCOMPLET, this.dateService.now());
		jpaDossier.addStatut(jpaStatutActuel);
		String contenu = "Incomplet!";
		jpaDossier
				.addMessage(new JpaMessage(jpaDossier, this.deposantBeta.toString(), this.dateService.now(), contenu));
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
		assertEquals(this.deposantBeta.toString(), jpaMessage.getAuteur());
	}

	@Test
	public void findByDeposantTest() throws Exception {
		JpaDossier jpaDossier = new JpaDossier(this.dossier.identity().toString(), this.jpaUser, EnumTypes.DPMI);
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
		JpaDossier jpaDossier = new JpaDossier(this.dossier.identity().toString(), this.jpaUser, EnumTypes.DPMI);
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
		JpaDossier jpaDossier = new JpaDossier(this.dossier.identity().toString(), this.jpaUser, EnumTypes.DPMI);
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
		assertEquals(this.jpaUser.getId(), dossier.get().deposant().identity().toString());
	}

	@Test
	public void deleteTest() throws Exception {
		JpaDossier jpaDossier = new JpaDossier(this.dossier.identity().toString(), this.jpaUser, EnumTypes.DPMI);
		jpaDossier.addStatut(new JpaStatut(jpaDossier, EnumStatuts.DEPOSE, this.dateService.now()));
		jpaDossier.addStatut(new JpaStatut(jpaDossier, EnumStatuts.QUALIFIE, this.dateService.now()));
		jpaDossier.addStatut(new JpaStatut(jpaDossier, EnumStatuts.INSTRUCTION, this.dateService.now()));
		JpaStatut jpaStatutActuel = new JpaStatut(jpaDossier, EnumStatuts.INCOMPLET, this.dateService.now());
		jpaDossier.addStatut(jpaStatutActuel);
		String contenu = "Incomplet!";
		jpaDossier
				.addMessage(new JpaMessage(jpaDossier, this.deposantBeta.toString(), this.dateService.now(), contenu));
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