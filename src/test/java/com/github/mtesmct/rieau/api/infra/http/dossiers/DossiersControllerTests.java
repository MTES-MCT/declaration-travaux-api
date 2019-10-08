package com.github.mtesmct.rieau.api.infra.http.dossiers;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.github.mtesmct.rieau.api.application.dossiers.CodeCerfaNotFoundException;
import com.github.mtesmct.rieau.api.application.dossiers.DossierImportException;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.PieceJointe;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.CommuneNotFoundException;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithInstructeurNonBetaDetails;
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
@WithDeposantBetaDetails
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DossiersControllerTests {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private DossierRepository dossierRepository;
	@Autowired
	@Qualifier("dateTimeConverter")
	private DateConverter dateConverter;
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

	@BeforeEach
	public void setup() throws IOException, CommuneNotFoundException {
		this.uri = DossiersController.ROOT_URI;
		File file = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
		Fichier fichier = this.fichierFactory.creer(file, "application/pdf");
		this.fichierService.save(fichier);
		Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01", new ParcelleCadastrale("0","1","2"), true);
		projet.localisation().ajouterParcelle(new ParcelleCadastrale("3","4","5"));
		assertEquals(2, projet.localisation().parcellesCadastrales().size());
		this.dossier = this.dossierFactory.creer(this.deposantBeta, TypesDossier.DP, projet);
		dossier.ajouterCerfa(fichier.identity());
		file = new File("src/test/fixtures/dummy.pdf");
		fichier = this.fichierFactory.creer(file, "application/pdf");
		this.fichierService.save(fichier);
		this.pieceJointe = dossier.ajouter("1", fichier.identity());
		assertTrue(this.pieceJointe.isPresent());
		this.dossier = this.dossierRepository.save(this.dossier);
		assertNotNull(this.dossier);
		assertNotNull(this.dossier.identity());
		assertNotNull(this.dossier.deposant());
		assertNotNull(this.dossier.type());
		assertEquals(this.dossier.deposant().identity(), this.deposantBeta.identity());
	}

	@Test
	public void listerTest() throws Exception {
		this.mvc.perform(get(this.uri).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", equalTo(this.dossier.identity().toString())))
				.andExpect(jsonPath("$[0].type", equalTo(this.dossier.type().type().toString())))
				.andExpect(jsonPath("$[0].statut", equalTo(this.dossier.statut().toString())))
				.andExpect(jsonPath("$[0].date", equalTo(this.dateConverter.format((this.dossier.dateDepot())))))
				.andExpect(jsonPath("$[0].piecesAJoindre").isArray())
				.andExpect(jsonPath("$[0].piecesAJoindre").isNotEmpty())
				.andExpect(jsonPath("$[0].piecesAJoindre", hasSize(1)))
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
	public void consulterTest() throws Exception {
		this.mvc.perform(get(this.uri + "/" + this.dossier.identity().toString()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.id", equalTo(this.dossier.identity().toString())))
				.andExpect(jsonPath("$.type", equalTo(this.dossier.type().type().toString())))
				.andExpect(jsonPath("$.statut", equalTo(this.dossier.statut().toString())))
				.andExpect(jsonPath("$.date", equalTo(this.dateConverter.format(this.dossier.dateDepot()))))
				.andExpect(jsonPath("$.piecesAJoindre").isArray()).andExpect(jsonPath("$.piecesAJoindre").isNotEmpty())
				.andExpect(jsonPath("$.piecesAJoindre", hasSize(1)))
				.andExpect(jsonPath("$.piecesAJoindre", equalTo(this.dossier.piecesAJoindre())))
				.andExpect(jsonPath("$.projet.nouvelleConstruction", equalTo(this.dossier.projet().nature().nouvelleConstruction())))
				.andExpect(jsonPath("$.projet.adresse.commune", equalTo(this.dossier.projet().localisation().adresse().commune().nom())))
				.andExpect(jsonPath("$.projet.adresse.codePostal", equalTo(this.dossier.projet().localisation().adresse().commune().codePostal())))
				.andExpect(jsonPath("$.projet.adresse.numero", equalTo(this.dossier.projet().localisation().adresse().numero())))
				.andExpect(jsonPath("$.projet.adresse.voie", equalTo(this.dossier.projet().localisation().adresse().voie())))
				.andExpect(jsonPath("$.projet.adresse.lieuDit", equalTo(this.dossier.projet().localisation().adresse().lieuDit())))
				.andExpect(jsonPath("$.projet.adresse.bp", equalTo(this.dossier.projet().localisation().adresse().bp())))
				.andExpect(jsonPath("$.projet.adresse.cedex", equalTo(this.dossier.projet().localisation().adresse().cedex())))
				.andExpect(jsonPath("$.projet.adresse.parcelles").isArray()).andExpect(jsonPath("$.piecesAJoindre").isNotEmpty())
				.andExpect(jsonPath("$.projet.adresse.parcelles", hasSize(2)))
				.andExpect(jsonPath("$.projet.adresse.parcelles", containsInAnyOrder(this.dossier.projet().localisation().parcellesCadastrales().stream().map(ParcelleCadastrale::toFlatString).toArray(String[]::new))));
	}

	@Test
	public void ajouterCerfaAutoriseTest() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.pdf", "application/pdf",
				"Spring Framework".getBytes());
		Mockito.when(this.mockImporterCerfaService.execute(any(), anyString(), anyString(), anyLong()))
				.thenReturn(Optional.ofNullable(this.dossier));
		this.mvc.perform(multipart(this.uri).file(multipartFile)).andExpect(status().isOk());
	}

	@Test
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
	public void ajouterPieceJointeAutoriseTest() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.pdf", "application/pdf",
				"Spring Framework".getBytes());
		Mockito.when(this.mockAjouterPieceJointeService.execute(any(), anyString(), any(), anyString(), anyString(),
				anyLong())).thenReturn(this.pieceJointe);
		this.mvc.perform(
				multipart(this.uri + "/" + this.dossier.identity().toString() + "/piecesjointes/1").file(multipartFile))
				.andExpect(status().isOk());
	}

	@Test
	@WithInstructeurNonBetaDetails
	public void ajouterPieceJointeInterditTest() throws Exception {
		MockMultipartFile multipartFile = new MockMultipartFile("file", "test.pdf", "application/pdf",
				"Spring Framework".getBytes());
		this.mvc.perform(
				multipart(this.uri + "/" + this.dossier.identity().toString() + "/piecesjointes/1").file(multipartFile))
				.andExpect(status().isForbidden());
	}

}