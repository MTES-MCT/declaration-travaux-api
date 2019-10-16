package com.github.mtesmct.rieau.api.infra.http;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumStatuts;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithInstructeurNonBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithMairieBetaDetails;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.infra.http.dossiers.DossiersController;
import com.github.mtesmct.rieau.api.infra.http.dossiers.JsonDossier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.RestAssured;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DossiersControllerIT {

	@Autowired
	private DossierRepository dossierRepository;
	@Autowired
	@Qualifier("dateTimeConverter")
	private DateConverter dateConverter;
	@Autowired
	private DossierFactory dossierFactory;
	@Autowired
	private FichierService fichierService;
	@Autowired
	private FichierFactory fichierFactory;
	@Autowired
	private ProjetFactory projetFactory;

	private Dossier dossier;

	@Autowired
	@Qualifier("deposantBeta")
	private Personne deposantBeta;
	private Fichier fichier;
	private File cerfa;
	private File dp1;

	@Autowired
	private KeycloakTestsHelper keycloakTestsHelper;
	@LocalServerPort
	private int serverPort;
	private String deposantAccessToken;
	private String mairieAccessToken;
	private String forbiddenToken;
	private String invalidToken = "invalid-token";

	@BeforeEach
	public void initData() throws Exception {
		cerfa = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
		fichier = this.fichierFactory.creer(cerfa, "application/pdf");
		fichierService.save(fichier);
		Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01",
				new ParcelleCadastrale("0", "1", "2"), true, true);
		dossier = dossierFactory.creer(deposantBeta, EnumTypes.DPMI, projet, fichier.identity());
		dossier = dossierRepository.save(dossier);
		assertNotNull(dossier);
		assertNotNull(dossier.identity());
		assertNotNull(dossier.deposant());
		assertNotNull(dossier.type());
		assertEquals(deposantBeta.identity(), dossier.deposant().identity());
		dp1 = new File("src/test/fixtures/dummy.pdf");
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		this.deposantAccessToken = this.keycloakTestsHelper.getAccessToken(WithDeposantBetaDetails.ID,
				WithDeposantBetaDetails.ID);
		this.mairieAccessToken = this.keycloakTestsHelper.getAccessToken(WithMairieBetaDetails.ID,
				WithMairieBetaDetails.ID);
		this.forbiddenToken = this.keycloakTestsHelper.getAccessToken(WithInstructeurNonBetaDetails.ID,
				WithInstructeurNonBetaDetails.ID);
	}

	@Test
	public void listerDeposantTest() throws Exception {
		List<JsonDossier> dossiers = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.deposantAccessToken).expect().statusCode(200).when().get().then().assertThat()
				.and().extract().jsonPath().getList("", JsonDossier.class);
		assertFalse(dossiers.isEmpty());
		assertEquals(1, dossiers.size());
		assertEquals(dossiers.get(0).getId(), this.dossier.identity().toString());
	}

	@Test
	public void listerMairieTest() throws Exception {
		List<JsonDossier> dossiers = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.mairieAccessToken).expect().statusCode(200).when().get().then().assertThat()
				.and().extract().jsonPath().getList("", JsonDossier.class);
		assertFalse(dossiers.isEmpty());
		assertEquals(1, dossiers.size());
		assertEquals(dossiers.get(0).getId(), this.dossier.identity().toString());
	}

	@Test
	public void listerNonAuthentifieTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.invalidToken).expect().statusCode(401).when().get();
	}

	@Test
	public void listerInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.forbiddenToken).expect().statusCode(403).when().get();
	}

	@Test
	public void consulterDeposantTest() throws Exception {
		JsonDossier jsonDossier = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.deposantAccessToken).expect().statusCode(200).when()
				.get("/{id}", this.dossier.identity().toString()).then().assertThat().and().extract().jsonPath()
				.getObject("", JsonDossier.class);
		assertNotNull(jsonDossier);
		assertEquals(jsonDossier.getId(), this.dossier.identity().toString());
	}

	@Test
	public void consulterMairieTest() throws Exception {
		JsonDossier jsonDossier = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.mairieAccessToken).expect().statusCode(200).when()
				.get("/{id}", this.dossier.identity().toString()).then().assertThat().and().extract().jsonPath()
				.getObject("", JsonDossier.class);
		assertNotNull(jsonDossier);
		assertEquals(jsonDossier.getId(), this.dossier.identity().toString());
	}

	@Test
	public void consulterNonAuthentifieTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.invalidToken).expect().statusCode(401).when()
				.get("/{id}", this.dossier.identity().toString());
	}

	@Test
	public void consulterInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.forbiddenToken).expect().statusCode(403).when()
				.get("/{id}", this.dossier.identity().toString());
	}

	@Test
	public void ajouterCerfaTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.deposantAccessToken).multiPart("file", this.cerfa, "application/pdf").expect()
				.statusCode(200).when().post();
		List<Dossier> dossiers = this.dossierRepository.findByDeposantId(this.deposantBeta.identity().toString());
		Optional<Dossier> dossierLu = dossiers.stream().filter(d -> !d.identity().equals(this.dossier.identity()))
				.findAny();
		assertTrue(dossierLu.isPresent());
		assertNotNull(dossierLu.get().cerfa());
		assertNotNull(dossierLu.get().cerfa().code());
		assertEquals(EnumTypes.DPMI, dossierLu.get().cerfa().code().type());
		assertFalse(dossierLu.get().projet().nature().nouvelleConstruction());
		assertFalse(dossierLu.get().projet().localisation().lotissement());
		assertIterableEquals(Arrays.asList(new String[] { "1" }), dossierLu.get().piecesAJoindre());
		assertEquals("1", dossierLu.get().projet().localisation().adresse().numero());
		assertEquals("Route de Kerrivaud", dossierLu.get().projet().localisation().adresse().voie());
		assertEquals("", dossierLu.get().projet().localisation().adresse().lieuDit());
		assertEquals("44500", dossierLu.get().projet().localisation().adresse().commune().codePostal());
		assertEquals("", dossierLu.get().projet().localisation().adresse().bp());
		assertEquals(1, dossierLu.get().projet().localisation().parcellesCadastrales().size());
		assertEquals("000-CT-0099",
				dossierLu.get().projet().localisation().parcellesCadastrales().get(0).toFlatString());
	}

	@Test
	public void ajouterCerfaNonAuthentifieTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.invalidToken).multiPart("file", this.cerfa).expect().statusCode(401).when().post();
	}

	@Test
	public void ajouterCerfaInconnuInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.forbiddenToken).multiPart("file", this.cerfa).expect().statusCode(403).when().post();
	}

	@Test
	public void ajouterCerfaMairieInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.mairieAccessToken).multiPart("file", this.cerfa).expect().statusCode(403).when().post();
	}

	@Test
	public void ajouterPieceJointeTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.deposantAccessToken).multiPart("file", this.dp1).expect().statusCode(200).when()
				.post("/{id}/piecesjointes/{numero}", this.dossier.identity().toString(), "1");
		Optional<Dossier> dossierLu = this.dossierRepository.findById(dossier.identity().toString());
		assertTrue(dossierLu.isPresent());
		assertNotNull(dossierLu.get().pieceJointes());
		assertFalse(dossierLu.get().pieceJointes().isEmpty());
		assertEquals(1, dossierLu.get().pieceJointes().size());
		assertEquals("1", dossierLu.get().pieceJointes().get(0).code().numero());
		assertEquals(EnumTypes.DPMI, dossierLu.get().pieceJointes().get(0).code().type());
	}

	@Test
	public void ajouterPieceJointeNonAuthentifieTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.invalidToken).multiPart("file", this.dp1).expect().statusCode(401).when()
				.post("/{id}/piecesjointes/{numero}", this.dossier.identity().toString(), "1");
	}

	@Test
	public void ajouterPieceJointeInconnuInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.forbiddenToken).multiPart("file", this.dp1).expect().statusCode(403).when()
				.post("/{id}/piecesjointes/{numero}", this.dossier.identity().toString(), "1");
	}
	@Test
	public void ajouterPieceJointeMairieInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.mairieAccessToken).multiPart("file", this.dp1).expect().statusCode(403).when()
				.post("/{id}/piecesjointes/{numero}", this.dossier.identity().toString(), "1");
	}

	@Test
	public void qualifierMairieTest() throws Exception {
		JsonDossier jsonDossier = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.mairieAccessToken).expect().statusCode(200).when()
				.post("/{id}/qualifier", this.dossier.identity().toString()).then().assertThat().and().extract().jsonPath()
				.getObject("", JsonDossier.class);
		assertNotNull(jsonDossier);
		assertEquals(this.dossier.identity().toString(), jsonDossier.getId());
		assertFalse(jsonDossier.getStatuts().isEmpty());
		assertEquals(2, jsonDossier.getStatuts().size());
		assertEquals(EnumStatuts.DEPOSE.toString(), jsonDossier.getStatuts().get(0).getId());
		assertEquals(EnumStatuts.QUALIFIE.toString(), jsonDossier.getStatuts().get(1).getId());
	}

	@Test
	public void qualifierDeposantInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.deposantAccessToken).expect().statusCode(403).when()
				.post("/{id}/qualifier", this.dossier.identity().toString());
	}
}