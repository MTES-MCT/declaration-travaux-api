package com.github.mtesmct.rieau.api.infra.http;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.ParcelleCadastrale;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Projet;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithInstructeurNonBetaDetails;
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
	private String accessToken;
	private String forbiddenToken;
	private String invalidToken = "invalid-token";

	@BeforeEach
	public void initData() throws Exception {
		cerfa = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
		fichier = this.fichierFactory.creer(cerfa, "application/pdf");
		fichierService.save(fichier);
		Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01",
				new ParcelleCadastrale("0", "1", "2"), true);
		dossier = dossierFactory.creer(deposantBeta, TypesDossier.DP, projet);
		dossier.ajouterCerfa(fichier.identity());
		dossier = dossierRepository.save(dossier);
		assertNotNull(dossier);
		assertNotNull(dossier.identity());
		assertNotNull(dossier.deposant());
		assertNotNull(dossier.type());
		assertEquals(dossier.deposant().identity(), deposantBeta.identity());
		dp1 = new File("src/test/fixtures/dummy.pdf");
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		this.accessToken = this.keycloakTestsHelper.getAccessToken(WithDeposantBetaDetails.ID,
				WithDeposantBetaDetails.ID);
		this.forbiddenToken = this.keycloakTestsHelper.getAccessToken(WithInstructeurNonBetaDetails.ID,
				WithInstructeurNonBetaDetails.ID);
	}

	@Test
	public void listerTest() throws Exception {
		List<JsonDossier> dossiers = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.accessToken).expect().statusCode(200).when().get().then().assertThat().and()
				.extract().jsonPath().getList("", JsonDossier.class);
		assertFalse(dossiers.isEmpty());
		assertEquals(1, dossiers.size());
		assertEquals(this.dossier.identity().toString(), dossiers.get(0).getId());
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
	public void consulterTest() throws Exception {
		JsonDossier jsonDossier = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.accessToken).expect().statusCode(200).when()
				.get("/{id}", this.dossier.identity().toString()).then().assertThat().and().extract().jsonPath()
				.getObject("", JsonDossier.class);
		assertNotNull(jsonDossier);
		assertEquals(this.dossier.identity().toString(), jsonDossier.getId());
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
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive().oauth2(this.accessToken)
				.multiPart("file", this.cerfa, "application/pdf").expect().statusCode(200).when().post();
		Optional<Dossier> dossierLu = this.dossierRepository.findById(dossier.identity().toString());
		assertTrue(dossierLu.isPresent());
		assertNotNull(dossierLu.get().cerfa());
		assertNotNull(dossierLu.get().cerfa().code());
		assertEquals(TypesDossier.DP, dossierLu.get().cerfa().code().type());
	}

	@Test
	public void ajouterCerfaNonAuthentifieTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.invalidToken).multiPart("file", this.cerfa).expect().statusCode(401).when().post();
	}

	@Test
	public void ajouterCerfainterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.forbiddenToken).multiPart("file", this.cerfa).expect().statusCode(403).when().post();
	}

	@Test
	public void ajouterPieceJointeTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive().oauth2(this.accessToken)
				.multiPart("file", this.dp1).expect().statusCode(200).when()
				.post("/{id}/piecesjointes/{numero}", this.dossier.identity().toString(), "1");
		Optional<Dossier> dossierLu = this.dossierRepository.findById(dossier.identity().toString());
		assertTrue(dossierLu.isPresent());
		assertNotNull(dossierLu.get().pieceJointes());
		assertFalse(dossierLu.get().pieceJointes().isEmpty());
		assertEquals(1, dossierLu.get().pieceJointes().size());
		assertEquals("1", dossierLu.get().pieceJointes().get(0).code().numero());
		assertEquals(TypesDossier.DP, dossierLu.get().pieceJointes().get(0).code().type());
	}

	@Test
	public void ajouterPieceJointeNonAuthentifieTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.invalidToken).multiPart("file", this.dp1).expect().statusCode(401).when()
				.post("/{id}/piecesjointes/{numero}", this.dossier.identity().toString(), "1");
	}

	@Test
	public void ajouterPieceJointeInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.forbiddenToken).multiPart("file", this.dp1).expect().statusCode(403).when()
				.post("/{id}/piecesjointes/{numero}", this.dossier.identity().toString(), "1");
	}
}