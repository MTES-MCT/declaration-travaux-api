package com.github.mtesmct.rieau.api.infra.http.dossiers;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.dossiers.CodeCerfaNotFoundException;
import com.github.mtesmct.rieau.api.application.dossiers.DossierImportException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.AjouterPieceJointeException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumStatuts;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceNonAJoindreException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.StatutForbiddenException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeDossierNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypeStatutNotFoundException;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.domain.services.FichierServiceException;
import com.github.mtesmct.rieau.api.domain.services.StatutService;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithInstructeurNonBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithMairieBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.dossiers.TxAjouterPieceJointeService;
import com.github.mtesmct.rieau.api.infra.application.dossiers.TxImporterCerfaService;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DossiersControllerTests {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private DossierRepository dossierRepository;
	@Autowired
	@Qualifier("dateTimeConverter")
	private DateConverter dateTimeConverter;
	@Autowired
	private DossierFactory dossierFactory;
	@Autowired
	private FichierService fichierService;
	@MockBean
	private TxImporterCerfaService mockImporterCerfaService;
	@MockBean
	private TxAjouterPieceJointeService mockAjouterPieceJointeService;
	@Autowired
	private FichierFactory fichierFactory;
	@Autowired
	private ProjetFactory projetFactory;

	private Dossier dossier;

	private Optional<PieceJointe> pieceJointe;

	private String uri;

	@Autowired
	@Qualifier("deposantBeta")
	private Personne deposantBeta;
	@Autowired
	@Qualifier("instructeurNonBeta")
	private Personne instructeur;
	@Autowired
	@Qualifier("autreDeposantBeta")
	private Personne autreDeposantBeta;
	@Autowired
	private StatutService statutService;

	@BeforeEach
	public void setup() throws IOException, CommuneNotFoundException, StatutForbiddenException,
			TypeStatutNotFoundException, PieceNonAJoindreException, FichierServiceException,
			AjouterPieceJointeException, TypeDossierNotFoundException {
		this.uri = DossiersController.ROOT_URI;
		File file = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
		Fichier fichier = this.fichierFactory.creer(file, "application/pdf");
		this.fichierService.save(fichier);
		Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01",
				new ParcelleCadastrale("0", "1", "2"), true, true);
		projet.localisation().ajouterParcelle(new ParcelleCadastrale("3", "4", "5"));
		assertEquals(2, projet.localisation().parcellesCadastrales().size());
		this.dossier = this.dossierFactory.creer(this.deposantBeta, EnumTypes.DPMI, projet, fichier.identity());
		file = new File("src/test/fixtures/dummy.pdf");
		fichier = this.fichierFactory.creer(file, "application/pdf");
		this.fichierService.save(fichier);
		this.pieceJointe = dossier.ajouterPieceJointe("1", fichier.identity());
		assertTrue(this.pieceJointe.isPresent());
		this.dossier = this.dossierRepository.save(this.dossier);
		assertNotNull(this.dossier);
		assertNotNull(this.dossier.identity());
		assertNotNull(this.dossier.deposant());
		assertNotNull(this.dossier.type());
		assertFalse(this.dossier.historiqueStatuts().isEmpty());
		assertEquals(1, this.dossier.historiqueStatuts().size());
		assertTrue(this.dossier.statutActuel().isPresent());
		assertEquals(this.deposantBeta.identity(), this.dossier.deposant().identity());
	}

	@Test
	@WithDeposantBetaDetails
	public void listerDeposantTest() throws Exception {
		this.mvc.perform(get(this.uri).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", equalTo(this.dossier.identity().toString())))
				.andExpect(jsonPath("$[0].type", equalTo(this.dossier.type().type().toString())))
				.andExpect(jsonPath("$[0].statutActuel.id",
						equalTo(this.dossier.statutActuel().get().type().identity().toString())))
				.andExpect(jsonPath("$[0].statutActuel.dateDebut",
						equalTo(this.dateTimeConverter.format((this.dossier.statutActuel().get().dateDebut())))))
				.andExpect(jsonPath("$[0].piecesAJoindre").isArray())
				.andExpect(jsonPath("$[0].piecesAJoindre").isNotEmpty())
				.andExpect(jsonPath("$[0].piecesAJoindre", hasSize(2)))
				.andExpect(jsonPath("$[0].piecesAJoindre", equalTo(this.dossier.piecesAJoindre())));
	}

	@Test
	@WithMairieBetaDetails
	public void listerMairieTest() throws Exception {
		File file = new File("src/test/fixtures/cerfa_13406_PCMI.pdf");
		Fichier fichier = this.fichierFactory.creer(file, "application/pdf");
		this.fichierService.save(fichier);
		Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44500", "BP 44", "Cedex 01",
				new ParcelleCadastrale("1", "2", "3"), true, true);
		projet.localisation().ajouterParcelle(new ParcelleCadastrale("4", "5", "6"));
		assertEquals(2, projet.localisation().parcellesCadastrales().size());
		Dossier dossier = this.dossierFactory.creer(this.autreDeposantBeta, EnumTypes.PCMI, projet, fichier.identity());
		file = new File("src/test/fixtures/dummy.pdf");
		fichier = this.fichierFactory.creer(file, "application/pdf");
		this.fichierService.save(fichier);
		this.pieceJointe = dossier.ajouterPieceJointe("1", fichier.identity());
		assertTrue(this.pieceJointe.isPresent());
		dossier = this.dossierRepository.save(this.dossier);
		this.mvc.perform(get(this.uri).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", equalTo(this.dossier.identity().toString())))
				.andExpect(jsonPath("$[0].type", equalTo(this.dossier.type().type().toString())))
				.andExpect(jsonPath("$[0].statutActuel.id",
						equalTo(this.dossier.statutActuel().get().type().identity().toString())))
				.andExpect(jsonPath("$[0].statutActuel.dateDebut",
						equalTo(this.dateTimeConverter.format((this.dossier.statutActuel().get().dateDebut())))))
				.andExpect(jsonPath("$[0].piecesAJoindre").isArray())
				.andExpect(jsonPath("$[0].piecesAJoindre").isNotEmpty())
				.andExpect(jsonPath("$[0].piecesAJoindre", hasSize(2)))
				.andExpect(jsonPath("$[0].piecesAJoindre", equalTo(this.dossier.piecesAJoindre())));
	}

	@Test
	@WithMockUser
	public void listerUserInconnuInterditTest() throws Exception {
		this.mvc.perform(get(this.uri).accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
	}

	@Test
	@WithAnonymousUser
	public void listerUserAnonymeInterditTest() throws Exception {
		this.mvc.perform(get(this.uri).accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
	}

	@Test
	@WithDeposantBetaDetails
	public void consulterTest() throws Exception {
		this.mvc.perform(get(this.uri + "/" + this.dossier.identity().toString()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.id", equalTo(this.dossier.identity().toString())))
				.andExpect(jsonPath("$.type", equalTo(this.dossier.type().type().toString())))
				.andExpect(jsonPath("$.statutActuel").isNotEmpty())
				.andExpect(jsonPath("$.statutActuel.id",
						equalTo(this.dossier.statutActuel().get().type().identity().toString())))
				.andExpect(jsonPath("$.statutActuel.libelle",
						equalTo(this.dossier.statutActuel().get().type().libelle())))
				.andExpect(jsonPath("$.statutActuel.dateDebut",
						equalTo(this.dateTimeConverter.format(this.dossier.statutActuel().get().dateDebut()))))
				.andExpect(jsonPath("$.statuts").isArray()).andExpect(jsonPath("$.statuts").isNotEmpty())
				.andExpect(jsonPath("$.statuts", hasSize(1)))
				.andExpect(jsonPath("$.statuts[0].id",
						equalTo(this.dossier.historiqueStatuts().get(0).type().identity().toString())))
				.andExpect(jsonPath("$.statuts[0].libelle",
						equalTo(this.dossier.historiqueStatuts().get(0).type().libelle())))
				.andExpect(jsonPath("$.statuts[0].dateDebut",
						equalTo(this.dateTimeConverter.format(this.dossier.historiqueStatuts().get(0).dateDebut()))))
				.andExpect(jsonPath("$.piecesAJoindre").isArray()).andExpect(jsonPath("$.piecesAJoindre").isNotEmpty())
				.andExpect(jsonPath("$.piecesAJoindre", hasSize(2)))
				.andExpect(jsonPath("$.piecesAJoindre", equalTo(this.dossier.piecesAJoindre())))
				.andExpect(jsonPath("$.cerfa").isNotEmpty())
				.andExpect(jsonPath("$.cerfa.type", equalTo(this.dossier.cerfa().code().type().toString())))
				.andExpect(jsonPath("$.cerfa.numero", equalTo(this.dossier.cerfa().code().numero())))
				.andExpect(jsonPath("$.cerfa.fichierId", equalTo(this.dossier.cerfa().fichierId().toString())))
				.andExpect(jsonPath("$.piecesJointes").isArray()).andExpect(jsonPath("$.piecesJointes").isNotEmpty())
				.andExpect(jsonPath("$.piecesJointes", hasSize(1)))
				.andExpect(jsonPath("$.piecesJointes[0].type",
						equalTo(this.dossier.pieceJointes().get(0).code().type().toString())))
				.andExpect(jsonPath("$.piecesJointes[0].numero",
						equalTo(this.dossier.pieceJointes().get(0).code().numero())))
				.andExpect(jsonPath("$.piecesJointes[0].fichierId",
						equalTo(this.dossier.pieceJointes().get(0).fichierId().toString())))
				.andExpect(jsonPath("$.projet.nouvelleConstruction",
						equalTo(this.dossier.projet().nature().nouvelleConstruction())))
				.andExpect(
						jsonPath("$.projet.lotissement", equalTo(this.dossier.projet().localisation().lotissement())))
				.andExpect(jsonPath("$.projet.adresse.commune",
						equalTo(this.dossier.projet().localisation().adresse().commune().nom())))
				.andExpect(jsonPath("$.projet.adresse.codePostal",
						equalTo(this.dossier.projet().localisation().adresse().commune().codePostal())))
				.andExpect(jsonPath("$.projet.adresse.numero",
						equalTo(this.dossier.projet().localisation().adresse().numero())))
				.andExpect(jsonPath("$.projet.adresse.voie",
						equalTo(this.dossier.projet().localisation().adresse().voie())))
				.andExpect(jsonPath("$.projet.adresse.lieuDit",
						equalTo(this.dossier.projet().localisation().adresse().lieuDit())))
				.andExpect(
						jsonPath("$.projet.adresse.bp", equalTo(this.dossier.projet().localisation().adresse().bp())))
				.andExpect(jsonPath("$.projet.adresse.cedex",
						equalTo(this.dossier.projet().localisation().adresse().cedex())))
				.andExpect(jsonPath("$.projet.adresse.parcelles").isArray())
				.andExpect(jsonPath("$.piecesAJoindre").isNotEmpty())
				.andExpect(jsonPath("$.projet.adresse.parcelles", hasSize(2)))
				.andExpect(jsonPath("$.projet.adresse.parcelles",
						containsInAnyOrder(this.dossier.projet().localisation().parcellesCadastrales().stream()
								.map(ParcelleCadastrale::toFlatString).toArray(String[]::new))));
	}

	@Test
	@WithDeposantBetaDetails
	public void ajouterCerfaAutoriseTest() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.pdf", "application/pdf",
				"Spring Framework".getBytes());
		Mockito.when(this.mockImporterCerfaService.execute(any(), anyString(), anyString(), anyLong()))
				.thenReturn(Optional.ofNullable(this.dossier));
		this.mvc.perform(multipart(this.uri).file(multipartFile)).andExpect(status().isOk());
	}

	@Test
	@WithDeposantBetaDetails
	public void ajouterCerfaIncorrectExceptionTest() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.pdf", "application/pdf",
				"Spring Framework".getBytes());
		Mockito.when(this.mockImporterCerfaService.execute(any(), anyString(), anyString(), anyLong()))
				.thenThrow(new DossierImportException(new CodeCerfaNotFoundException()));
		this.mvc.perform(multipart(this.uri).file(multipartFile)).andExpect(status().isInternalServerError())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.message",
						containsString(CodeCerfaNotFoundException.AUCUN_CODE_CERFA_TROUVE_DANS_LE_FICHIER_PDF)));
	}

	@Test
	@WithInstructeurNonBetaDetails
	public void ajouterCerfaInterditTest() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.pdf", "application/pdf",
				"Spring Framework".getBytes());
		this.mvc.perform(multipart(this.uri).file(multipartFile)).andExpect(status().isForbidden());
	}

	@Test
	@WithDeposantBetaDetails
	public void ajouterPieceJointeAutoriseTest() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.pdf", "application/pdf",
				"Spring Framework".getBytes());
		Mockito.when(this.mockAjouterPieceJointeService.execute(any(), anyString(), any(), anyString(), anyString(),
				anyLong())).thenReturn(this.pieceJointe);
		this.mvc.perform(multipart(
				this.uri + "/" + this.dossier.identity().toString() + DossiersController.PIECES_JOINTES_URI + "/1")
						.file(multipartFile))
				.andExpect(status().isOk());
	}

	@Test
	@WithInstructeurNonBetaDetails
	public void ajouterPieceJointeInterditTest() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.pdf", "application/pdf",
				"Spring Framework".getBytes());
		this.mvc.perform(multipart(
				this.uri + "/" + this.dossier.identity().toString() + DossiersController.PIECES_JOINTES_URI + "1")
						.file(multipartFile))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMairieBetaDetails
	public void qualifierMairieTest() throws Exception {
		this.mvc.perform(post(this.uri + "/" + this.dossier.identity().toString() + DossiersController.QUALIFIER_URI)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content("{ \"message\": \"Le dossier est incomplet car le plan de masse est illisible\"}")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.id", equalTo(this.dossier.identity().toString())))
				.andExpect(jsonPath("$.type", equalTo(this.dossier.type().type().toString())))
				.andExpect(jsonPath("$.statutActuel.id", equalTo(EnumStatuts.QUALIFIE.toString())));
	}

	@Test
	@WithDeposantBetaDetails
	public void qualifierDeposantInterditTest() throws Exception {
		this.mvc.perform(post(this.uri + "/" + this.dossier.identity().toString() + DossiersController.QUALIFIER_URI)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
	}

	@Test
	@WithInstructeurNonBetaDetails
	public void declarerIncompletInstructeurTest() throws Exception {
        this.statutService.qualifier(this.dossier);
        this.statutService.instruire(this.dossier);
		this.dossier = this.dossierRepository.save(this.dossier);
		String message = "Le dossier est incomplet car le plan de masse est illisible";
		this.mvc.perform(
				post(this.uri + "/" + this.dossier.identity().toString() + DossiersController.DECLARER_INCOMPLET_URI)
						.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_UTF8).param("message", message))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.id", equalTo(this.dossier.identity().toString())))
				.andExpect(jsonPath("$.type", equalTo(this.dossier.type().type().toString())))
				.andExpect(jsonPath("$.statutActuel.id", equalTo(EnumStatuts.INCOMPLET.toString())))
				.andExpect(jsonPath("$.statuts").isArray()).andExpect(jsonPath("$.statuts").isNotEmpty())
				.andExpect(jsonPath("$.statuts", hasSize(4)))
				.andExpect(jsonPath("$.messages").isArray()).andExpect(jsonPath("$.messages").isNotEmpty())
				.andExpect(jsonPath("$.messages", hasSize(1)))
				.andExpect(jsonPath("$.messages[0].auteur.id",
						equalTo(this.instructeur.identity().toString())))
				.andExpect(jsonPath("$.messages[0].auteur.email",
						equalTo(this.instructeur.email())))
				.andExpect(jsonPath("$.messages[0].contenu",
						equalTo(message)));
	}

	@Test
	@WithMairieBetaDetails
	public void declarerIncompletMairieInterditTest() throws Exception {
        this.statutService.qualifier(this.dossier);
        this.statutService.instruire(this.dossier);
        this.dossier = this.dossierRepository.save(this.dossier);
		this.mvc.perform(
				post(this.uri + "/" + this.dossier.identity().toString() + DossiersController.DECLARER_INCOMPLET_URI)
						.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_UTF8).param("message", "Le dossier est incomplet car le plan de masse est illisible"))
				.andExpect(status().isForbidden());
	}

}