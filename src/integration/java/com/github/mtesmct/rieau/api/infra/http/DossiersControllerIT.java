package com.github.mtesmct.rieau.api.infra.http;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumStatuts;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.EnumTypes;
import com.github.mtesmct.rieau.api.domain.entities.personnes.User;
import com.github.mtesmct.rieau.api.infra.application.auth.WithAutreDeposantBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithInstructeurNonBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithMairieBetaDetails;
import com.github.mtesmct.rieau.api.infra.http.dossiers.DossiersController;
import com.github.mtesmct.rieau.api.infra.http.dossiers.JsonDossier;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DossiersControllerIT {

	private JsonDossier jsonDossier;

	@Autowired
	@Qualifier("deposantBeta")
	private User deposantBeta;
	@Autowired
	@Qualifier("instructeurNonBeta")
	private User instructeur;
	private File cerfa;
	private File dp1;

	@Autowired
	private KeycloakTestsHelper keycloakTestsHelper;
	@LocalServerPort
	private int serverPort;
	private String deposantAccessToken;
	private String autreDeposantAccessToken;
	private String mairieAccessToken;
	private String instructeurAccessToken;
	private String invalidToken = "invalid-token";

	@BeforeEach
	public void initData() throws Exception {
		this.cerfa = new File("src/test/fixtures/cerfa_13406_PCMI.pdf");
		this.dp1 = new File("src/test/fixtures/dummy.pdf");
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		this.deposantAccessToken = this.keycloakTestsHelper.getAccessToken(WithDeposantBetaDetails.ID,
				WithDeposantBetaDetails.ID);
		this.mairieAccessToken = this.keycloakTestsHelper.getAccessToken(WithMairieBetaDetails.ID,
				WithMairieBetaDetails.ID);
		this.instructeurAccessToken = this.keycloakTestsHelper.getAccessToken(WithInstructeurNonBetaDetails.ID,
				WithInstructeurNonBetaDetails.ID);
		this.autreDeposantAccessToken = this.keycloakTestsHelper.getAccessToken(WithAutreDeposantBetaDetails.ID,
				WithAutreDeposantBetaDetails.ID);
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.deposantAccessToken).multiPart("file", this.cerfa, "application/pdf").expect()
				.statusCode(200).when().post();
		List<JsonDossier> dossiers = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.deposantAccessToken).expect().statusCode(200).when().get().then().assertThat()
				.and().extract().jsonPath().getList("", JsonDossier.class);
		assertFalse(dossiers.isEmpty());
		assertEquals(1, dossiers.size());
		this.jsonDossier = dossiers.get(0);
		assertNotNull(this.jsonDossier.getCerfa());
		assertEquals(EnumStatuts.DEPOSE.toString(), this.jsonDossier.getStatutActuel().getId());
	}

	@Test
	public void listerMairieTest() throws Exception {
		List<JsonDossier> dossiers = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.mairieAccessToken).expect().statusCode(200).when().get().then().assertThat()
				.and().extract().jsonPath().getList("", JsonDossier.class);
		assertFalse(dossiers.isEmpty());
		assertEquals(1, dossiers.size());
	}

	@Test
	public void listerNonAuthentifieTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.invalidToken).expect().statusCode(401).when().get();
	}

	@Test
	public void consulterDeposantTest() throws Exception {
		JsonDossier jsonDossier = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.deposantAccessToken).expect().statusCode(200).when()
				.get("/{id}", this.jsonDossier.getId()).then().assertThat().and().extract().jsonPath()
				.getObject("", JsonDossier.class);
		assertNotNull(jsonDossier);
		assertEquals(jsonDossier.getId(), this.jsonDossier.getId());
	}

	@Test
	public void consulterMairieTest() throws Exception {
		JsonDossier jsonDossier = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.mairieAccessToken).expect().statusCode(200).when()
				.get("/{id}", this.jsonDossier.getId()).then().assertThat().and().extract().jsonPath()
				.getObject("", JsonDossier.class);
		assertNotNull(jsonDossier);
		assertEquals(jsonDossier.getId(), this.jsonDossier.getId());
	}

	@Test
	public void consulterNonAuthentifieTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.invalidToken).expect().statusCode(401).when().get("/{id}", this.jsonDossier.getId());
	}

	@Test
	public void ajouterCerfaTest() throws Exception {
		assertEquals(EnumTypes.PCMI.toString(), this.jsonDossier.getCerfa().getType());
		assertTrue(this.jsonDossier.getProjet().isNouvelleConstruction());
		assertIterableEquals(Arrays.asList(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }),
				this.jsonDossier.getPiecesAJoindre());
		assertEquals("1", this.jsonDossier.getProjet().getAdresse().getNumero());
		assertEquals("Rue des Dervallières", this.jsonDossier.getProjet().getAdresse().getVoie());
		assertEquals("", this.jsonDossier.getProjet().getAdresse().getLieuDit());
		assertEquals("44100", this.jsonDossier.getProjet().getAdresse().getCodePostal());
		assertEquals("", this.jsonDossier.getProjet().getAdresse().getBp());
		assertEquals(1, this.jsonDossier.getProjet().getAdresse().getParcelles().length);
		assertEquals("000-LV-0040", this.jsonDossier.getProjet().getAdresse().getParcelles()[0]);
	}

	@Test
	public void ajouterCerfaNonAuthentifieTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.invalidToken).multiPart("file", this.cerfa).expect().statusCode(401).when().post();
	}

	@Test
	public void ajouterCerfaInconnuInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.instructeurAccessToken).multiPart("file", this.cerfa).expect().statusCode(403).when()
				.post();
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
				.post("/{id}" + DossiersController.PIECES_JOINTES_URI + "/{numero}", this.jsonDossier.getId(), "1");
		JsonDossier jsonDossier = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.deposantAccessToken).expect().statusCode(200).when()
				.get("/{id}", this.jsonDossier.getId()).then().assertThat().and().extract().jsonPath()
				.getObject("", JsonDossier.class);
		assertNotNull(jsonDossier);
		assertNotNull(jsonDossier.getPiecesJointes());
		assertFalse(jsonDossier.getPiecesJointes().isEmpty());
		assertEquals(1, jsonDossier.getPiecesJointes().size());
		assertEquals("1", jsonDossier.getPiecesJointes().get(0).getNumero());
		assertEquals(EnumTypes.PCMI.toString(), jsonDossier.getPiecesJointes().get(0).getType());
	}

	@Test
	public void ajouterPieceJointeNonAuthentifieTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.invalidToken).multiPart("file", this.dp1).expect().statusCode(401).when()
				.post("/{id}" + DossiersController.PIECES_JOINTES_URI + "/{numero}", this.jsonDossier.getId(), "1");
	}

	@Test
	public void ajouterPieceJointeInconnuInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.instructeurAccessToken).multiPart("file", this.dp1).expect().statusCode(403).when()
				.post("/{id}" + DossiersController.PIECES_JOINTES_URI + "/{numero}", this.jsonDossier.getId(), "1");
	}

	@Test
	public void ajouterPieceJointeMairieInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.mairieAccessToken).multiPart("file", this.dp1).expect().statusCode(403).when()
				.post("/{id}" + DossiersController.PIECES_JOINTES_URI + "/{numero}", this.jsonDossier.getId(), "1");
	}

	@Test
	public void qualifierMairieTest() throws Exception {
		JsonDossier jsonDossier = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.mairieAccessToken).expect().statusCode(200).when()
				.post("/{id}" + DossiersController.QUALIFIER_URI, this.jsonDossier.getId()).then().assertThat().and()
				.extract().jsonPath().getObject("", JsonDossier.class);
		assertNotNull(jsonDossier);
		assertFalse(jsonDossier.getStatuts().isEmpty());
		assertEquals(2, jsonDossier.getStatuts().size());
		assertEquals(EnumStatuts.DEPOSE.toString(), jsonDossier.getStatuts().get(0).getId());
		assertEquals(EnumStatuts.QUALIFIE.toString(), jsonDossier.getStatuts().get(1).getId());
	}

	@Test
	public void qualifierDeposantInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.deposantAccessToken).expect().statusCode(403).when()
				.post("/{id}" + DossiersController.QUALIFIER_URI, this.jsonDossier.getId());
	}

	@Test
	public void declarerIncompletInstructeurTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.mairieAccessToken).expect().statusCode(200).when()
				.post("/{id}" + DossiersController.QUALIFIER_URI, this.jsonDossier.getId()).then().assertThat().and()
				.extract().jsonPath().getObject("", JsonDossier.class);
		String message = "Le dossier est incomplet car le plan de masse est illisible";
		JsonDossier jsonDossier = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.instructeurAccessToken).contentType(ContentType.JSON).body(message).expect()
				.statusCode(200).when()
				.post("/{id}" + DossiersController.DECLARER_INCOMPLET_URI, this.jsonDossier.getId()).then().assertThat()
				.and().extract().jsonPath().getObject("", JsonDossier.class);
		assertNotNull(jsonDossier);
		assertEquals(this.jsonDossier.getId(), jsonDossier.getId());
		assertEquals(EnumStatuts.INCOMPLET.toString(), jsonDossier.getStatutActuel().getId());
		assertFalse(jsonDossier.getStatuts().isEmpty());
		assertEquals(3, jsonDossier.getStatuts().size());
		assertEquals(EnumStatuts.DEPOSE.toString(), jsonDossier.getStatuts().get(0).getId());
		assertEquals(EnumStatuts.QUALIFIE.toString(), jsonDossier.getStatuts().get(1).getId());
		assertEquals(EnumStatuts.INCOMPLET.toString(), jsonDossier.getStatuts().get(2).getId());
		assertFalse(jsonDossier.getMessages().isEmpty());
		assertEquals(1, jsonDossier.getMessages().size());
		assertEquals(message, jsonDossier.getMessages().get(0).getContenu());
		assertEquals(this.instructeur.identity().toString(), jsonDossier.getMessages().get(0).getAuteur().getId());
		assertEquals(this.instructeur.identite().nom(), jsonDossier.getMessages().get(0).getAuteur().getNom());
		assertEquals(this.instructeur.identite().prenom(), jsonDossier.getMessages().get(0).getAuteur().getPrenom());
		assertEquals(String.join(",", this.instructeur.profils()),
				String.join(",", jsonDossier.getMessages().get(0).getAuteur().getProfils()));
	}

	@Test
	public void declarerIncompletDeposantInterditTest() throws Exception {
		String message = "Le dossier est incomplet car le plan de masse est illisible";
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.deposantAccessToken).body(message).expect().statusCode(403).when()
				.post("/{id}" + DossiersController.DECLARER_INCOMPLET_URI, this.jsonDossier.getId());
	}

	@Test
	public void declarerCompletInstructeurTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.mairieAccessToken).expect().statusCode(200).when()
				.post("/{id}" + DossiersController.QUALIFIER_URI, this.jsonDossier.getId()).then().assertThat().and()
				.extract().jsonPath().getObject("", JsonDossier.class);
		String message = "Le dossier est incomplet car le plan de masse est illisible";
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.instructeurAccessToken).contentType(ContentType.JSON).body(message).expect()
				.statusCode(200).when()
				.post("/{id}" + DossiersController.DECLARER_INCOMPLET_URI, this.jsonDossier.getId()).then().assertThat()
				.and().extract().jsonPath().getObject("", JsonDossier.class);
		JsonDossier jsonDossier = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.instructeurAccessToken).expect().statusCode(200).when()
				.post("/{id}" + DossiersController.DECLARER_COMPLET_URI, this.jsonDossier.getId()).then().assertThat()
				.and().extract().jsonPath().getObject("", JsonDossier.class);
		assertNotNull(jsonDossier);
		assertEquals(this.jsonDossier.getId(), jsonDossier.getId());
		assertEquals(EnumStatuts.COMPLET.toString(), jsonDossier.getStatutActuel().getId());
		assertFalse(jsonDossier.getStatuts().isEmpty());
		assertEquals(4, jsonDossier.getStatuts().size());
		assertEquals(EnumStatuts.DEPOSE.toString(), jsonDossier.getStatuts().get(0).getId());
		assertEquals(EnumStatuts.QUALIFIE.toString(), jsonDossier.getStatuts().get(1).getId());
		assertEquals(EnumStatuts.INCOMPLET.toString(), jsonDossier.getStatuts().get(2).getId());
		assertEquals(EnumStatuts.COMPLET.toString(), jsonDossier.getStatuts().get(3).getId());
	}

	@Test
	public void declarerCompletDeposantInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.mairieAccessToken).expect().statusCode(200).when()
				.post("/{id}" + DossiersController.QUALIFIER_URI, this.jsonDossier.getId()).then().assertThat().and()
				.extract().jsonPath().getObject("", JsonDossier.class);
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.deposantAccessToken).expect().statusCode(403).when()
				.post("/{id}" + DossiersController.DECLARER_COMPLET_URI, this.jsonDossier.getId());
	}

	@Test
	public void prendreDecisionMairieTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.mairieAccessToken).expect().statusCode(200).when()
				.post("/{id}" + DossiersController.QUALIFIER_URI, this.jsonDossier.getId()).then().assertThat().and()
				.extract().jsonPath().getObject("", JsonDossier.class);
		String message = "Le dossier est incomplet car le plan de masse est illisible";
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.instructeurAccessToken).contentType(ContentType.JSON).body(message).expect()
				.statusCode(200).when()
				.post("/{id}" + DossiersController.DECLARER_INCOMPLET_URI, this.jsonDossier.getId()).then().assertThat()
				.and().extract().jsonPath().getObject("", JsonDossier.class);
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.instructeurAccessToken).expect().statusCode(200).when()
				.post("/{id}" + DossiersController.DECLARER_COMPLET_URI, this.jsonDossier.getId()).then().assertThat()
				.and().extract().jsonPath().getObject("", JsonDossier.class);
		JsonDossier jsonDossier = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.mairieAccessToken).multiPart("file", this.dp1).expect().statusCode(200).when()
				.post("/{id}" + DossiersController.PRENDRE_DECISION_URI, this.jsonDossier.getId()).then().assertThat()
				.and().extract().jsonPath().getObject("", JsonDossier.class);
		assertEquals(EnumStatuts.DECISION.toString(), jsonDossier.getStatutActuel().getId());
		assertNotNull(jsonDossier.getDecision());
	}

	@Test
	public void prendreDecisionDeposantInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.mairieAccessToken).expect().statusCode(200).when()
				.post("/{id}" + DossiersController.QUALIFIER_URI, this.jsonDossier.getId()).then().assertThat().and()
				.extract().jsonPath().getObject("", JsonDossier.class);
		String message = "Le dossier est incomplet car le plan de masse est illisible";
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.instructeurAccessToken).contentType(ContentType.JSON).body(message).expect()
				.statusCode(200).when()
				.post("/{id}" + DossiersController.DECLARER_INCOMPLET_URI, this.jsonDossier.getId()).then().assertThat()
				.and().extract().jsonPath().getObject("", JsonDossier.class);
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.instructeurAccessToken).expect().statusCode(200).when()
				.post("/{id}" + DossiersController.DECLARER_COMPLET_URI, this.jsonDossier.getId()).then().assertThat()
				.and().extract().jsonPath().getObject("", JsonDossier.class);
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.deposantAccessToken).multiPart("file", this.dp1).expect().statusCode(403).when()
				.post("/{id}" + DossiersController.PRENDRE_DECISION_URI, this.jsonDossier.getId());
	}

	@Test
	public void ajouterMessageInstructeurTest() throws Exception {
		JsonDossier jsonDossier = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.mairieAccessToken).expect().statusCode(200).when()
				.post("/{id}" + DossiersController.QUALIFIER_URI, this.jsonDossier.getId()).then().assertThat().and()
				.extract().jsonPath().getObject("", JsonDossier.class);
		assertEquals(2, jsonDossier.getStatuts().size());
		assertEquals(EnumStatuts.QUALIFIE.toString(), jsonDossier.getStatutActuel().getId());
		String message = "Le dossier est incomplet car le plan de masse est illisible";
		jsonDossier = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.instructeurAccessToken).contentType(ContentType.JSON).body(message).expect()
				.statusCode(200).when().post("/{id}" + DossiersController.MESSAGES_URI, this.jsonDossier.getId()).then()
				.assertThat().and().extract().jsonPath().getObject("", JsonDossier.class);
		assertNotNull(jsonDossier);
		assertFalse(jsonDossier.getStatuts().isEmpty());
		assertEquals(2, jsonDossier.getStatuts().size());
		assertEquals(EnumStatuts.DEPOSE.toString(), jsonDossier.getStatuts().get(0).getId());
		assertEquals(EnumStatuts.QUALIFIE.toString(), jsonDossier.getStatuts().get(1).getId());
		assertEquals(EnumStatuts.QUALIFIE.toString(), jsonDossier.getStatutActuel().getId());
		assertFalse(jsonDossier.getMessages().isEmpty());
		assertEquals(1, jsonDossier.getMessages().size());
		assertEquals(message, jsonDossier.getMessages().get(0).getContenu());
		assertEquals(this.instructeur.identity().toString(), jsonDossier.getMessages().get(0).getAuteur().getId());
		assertEquals(this.instructeur.identite().nom(), jsonDossier.getMessages().get(0).getAuteur().getNom());
		assertEquals(this.instructeur.identite().prenom(), jsonDossier.getMessages().get(0).getAuteur().getPrenom());
		assertEquals(String.join(",", this.instructeur.profils()),
				String.join(",", jsonDossier.getMessages().get(0).getAuteur().getProfils()));
	}

	@Test
	public void ajouterMessageDeposantTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.mairieAccessToken).expect().statusCode(200).when()
				.post("/{id}" + DossiersController.QUALIFIER_URI, this.jsonDossier.getId()).then().assertThat().and()
				.extract().jsonPath().getObject("", JsonDossier.class);
		String message = "Le dossier est incomplet car le plan de masse est illisible";
		JsonDossier jsonDossier = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.deposantAccessToken).contentType(ContentType.JSON).body(message).expect()
				.statusCode(200).when().post("/{id}" + DossiersController.MESSAGES_URI, this.jsonDossier.getId()).then()
				.assertThat().and().extract().jsonPath().getObject("", JsonDossier.class);
		assertNotNull(jsonDossier);
		assertEquals(this.jsonDossier.getId(), jsonDossier.getId());
		assertEquals(EnumStatuts.QUALIFIE.toString(), jsonDossier.getStatutActuel().getId());
		assertFalse(jsonDossier.getStatuts().isEmpty());
		assertEquals(2, jsonDossier.getStatuts().size());
		assertEquals(EnumStatuts.DEPOSE.toString(), jsonDossier.getStatuts().get(0).getId());
		assertEquals(EnumStatuts.QUALIFIE.toString(), jsonDossier.getStatuts().get(1).getId());
		assertFalse(jsonDossier.getMessages().isEmpty());
		assertEquals(1, jsonDossier.getMessages().size());
		assertEquals(message, jsonDossier.getMessages().get(0).getContenu());
		assertEquals(this.deposantBeta.identity().toString(), jsonDossier.getMessages().get(0).getAuteur().getId());
		assertEquals(this.deposantBeta.identite().nom(), jsonDossier.getMessages().get(0).getAuteur().getNom());
		assertEquals(this.deposantBeta.identite().prenom(), jsonDossier.getMessages().get(0).getAuteur().getPrenom());
		assertEquals(String.join(",", this.deposantBeta.profils()),
				String.join(",", jsonDossier.getMessages().get(0).getAuteur().getProfils()));
	}

	@Test
	public void ajouterMessageMairieInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.mairieAccessToken).expect().statusCode(200).when()
				.post("/{id}" + DossiersController.QUALIFIER_URI, this.jsonDossier.getId()).then().assertThat().and()
				.extract().jsonPath().getObject("", JsonDossier.class);
		String message = "Le dossier est incomplet car le plan de masse est illisible";
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.mairieAccessToken).body(message).expect().statusCode(403).when()
				.post("/{id}" + DossiersController.MESSAGES_URI, this.jsonDossier.getId());
	}

	@Test
	public void supprimerDeposantTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.deposantAccessToken).expect().statusCode(200).when()
				.delete("/{id}", this.jsonDossier.getId());
		List<JsonDossier> dossiers = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.deposantAccessToken).expect().statusCode(200).when().get().then().assertThat()
				.and().extract().jsonPath().getList("", JsonDossier.class);
		assertTrue(dossiers.isEmpty());
	}

	@Test
	public void supprimerDeposantAutreDossierInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.autreDeposantAccessToken).expect().statusCode(403).when()
				.delete("/{id}", this.jsonDossier.getId());
	}

	@Test
	public void supprimerInstructeurInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.instructeurAccessToken).expect().statusCode(403).when()
				.delete("/{id}", this.jsonDossier.getId());
	}

	@Test
	public void supprimerMairieInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.mairieAccessToken).expect().statusCode(403).when()
				.delete("/{id}", this.jsonDossier.getId());
	}
}