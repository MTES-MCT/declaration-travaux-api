package com.github.mtesmct.rieau.api.infra.http;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.github.mtesmct.rieau.api.domain.services.StatutService;
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
import io.restassured.http.ContentType;

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
	@Autowired
	private StatutService statutService;

	private Dossier dossier;

	@Autowired
	@Qualifier("deposantBeta")
	private Personne deposantBeta;
	@Autowired
	@Qualifier("instructeurNonBeta")
	private Personne instructeur;
	private Fichier fichier;
	private File cerfa;
	private File dp1;

	@Autowired
	private KeycloakTestsHelper keycloakTestsHelper;
	@LocalServerPort
	private int serverPort;
	private String deposantAccessToken;
	private String mairieAccessToken;
	private String instructeurAccessToken;
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
		this.instructeurAccessToken = this.keycloakTestsHelper.getAccessToken(WithInstructeurNonBetaDetails.ID,
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
				.oauth2(this.instructeurAccessToken).expect().statusCode(403).when().get();
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
				.oauth2(this.instructeurAccessToken).expect().statusCode(403).when()
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
				.oauth2(this.instructeurAccessToken).multiPart("file", this.cerfa).expect().statusCode(403).when().post();
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
				.post("/{id}"+DossiersController.PIECES_JOINTES_URI+"/{numero}", this.dossier.identity().toString(), "1");
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
				.post("/{id}"+DossiersController.PIECES_JOINTES_URI+"/{numero}", this.dossier.identity().toString(), "1");
	}

	@Test
	public void ajouterPieceJointeInconnuInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.instructeurAccessToken).multiPart("file", this.dp1).expect().statusCode(403).when()
				.post("/{id}"+DossiersController.PIECES_JOINTES_URI+"/{numero}", this.dossier.identity().toString(), "1");
	}
	@Test
	public void ajouterPieceJointeMairieInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.mairieAccessToken).multiPart("file", this.dp1).expect().statusCode(403).when()
				.post("/{id}"+DossiersController.PIECES_JOINTES_URI+"/{numero}", this.dossier.identity().toString(), "1");
	}

	@Test
	public void qualifierMairieTest() throws Exception {
		JsonDossier jsonDossier = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.mairieAccessToken).expect().statusCode(200).when()
				.post("/{id}"+DossiersController.QUALIFIER_URI, this.dossier.identity().toString()).then().assertThat().and().extract().jsonPath()
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
				.post("/{id}"+DossiersController.QUALIFIER_URI, this.dossier.identity().toString());
	}

	@Test
	public void declarerIncompletInstructeurTest() throws Exception {       
        this.statutService.qualifier(this.dossier);
		this.dossier = this.dossierRepository.save(this.dossier);
		String message = "Le dossier est incomplet car le plan de masse est illisible";
		JsonDossier jsonDossier = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.instructeurAccessToken).contentType(ContentType.JSON).body(message).expect().statusCode(200).when()
				.post("/{id}"+DossiersController.DECLARER_INCOMPLET_URI, this.dossier.identity().toString()).then().assertThat().and().extract().jsonPath()
				.getObject("", JsonDossier.class);
		assertNotNull(jsonDossier);
		assertEquals(this.dossier.identity().toString(), jsonDossier.getId());
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
		assertEquals(this.instructeur.email(), jsonDossier.getMessages().get(0).getAuteur().getEmail());
	}

	@Test
	public void declarerIncompletDeposantInterditTest() throws Exception {
    	String message = "Le dossier est incomplet car le plan de masse est illisible";
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.deposantAccessToken).body(message).expect().statusCode(403).when()
				.post("/{id}"+DossiersController.DECLARER_INCOMPLET_URI, this.dossier.identity().toString());
	}
	
	@Test
	public void instruireInstructeurTest() throws Exception {        
        this.statutService.qualifier(this.dossier);
        this.statutService.declarerIncomplet(this.dossier, this.instructeur, "Incomplet!");
		this.dossier = this.dossierRepository.save(this.dossier);
		JsonDossier jsonDossier = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.instructeurAccessToken).expect().statusCode(200).when()
				.post("/{id}"+DossiersController.INSTRUIRE_URI, this.dossier.identity().toString()).then().assertThat().and().extract().jsonPath()
				.getObject("", JsonDossier.class);
		assertNotNull(jsonDossier);
		assertEquals(this.dossier.identity().toString(), jsonDossier.getId());
		assertEquals(EnumStatuts.INSTRUCTION.toString(), jsonDossier.getStatutActuel().getId());
		assertFalse(jsonDossier.getStatuts().isEmpty());
		assertEquals(4, jsonDossier.getStatuts().size());
		assertEquals(EnumStatuts.DEPOSE.toString(), jsonDossier.getStatuts().get(0).getId());
		assertEquals(EnumStatuts.QUALIFIE.toString(), jsonDossier.getStatuts().get(1).getId());
		assertEquals(EnumStatuts.INCOMPLET.toString(), jsonDossier.getStatuts().get(2).getId());
		assertEquals(EnumStatuts.INSTRUCTION.toString(), jsonDossier.getStatuts().get(3).getId());
		assertFalse(jsonDossier.getMessages().isEmpty());
		assertEquals(1, jsonDossier.getMessages().size());
	}
	
	@Test
	public void instruireDeposantInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.deposantAccessToken).expect().statusCode(403).when()
				.post("/{id}"+DossiersController.INSTRUIRE_URI, this.dossier.identity().toString());
	}
	
	@Test
	public void declarerCompletInstructeurTest() throws Exception {        
        this.statutService.qualifier(this.dossier);        
        this.statutService.declarerIncomplet(this.dossier, this.instructeur, "Incomplet!");
        this.statutService.instruire(this.dossier);        
		this.dossier = this.dossierRepository.save(this.dossier);
		JsonDossier jsonDossier = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.instructeurAccessToken).expect().statusCode(200).when()
				.post("/{id}"+DossiersController.DECLARER_COMPLET_URI, this.dossier.identity().toString()).then().assertThat().and().extract().jsonPath()
				.getObject("", JsonDossier.class);
		assertNotNull(jsonDossier);
		assertEquals(this.dossier.identity().toString(), jsonDossier.getId());
		assertEquals(EnumStatuts.COMPLET.toString(), jsonDossier.getStatutActuel().getId());
		assertFalse(jsonDossier.getStatuts().isEmpty());
		assertEquals(5, jsonDossier.getStatuts().size());
		assertEquals(EnumStatuts.DEPOSE.toString(), jsonDossier.getStatuts().get(0).getId());
		assertEquals(EnumStatuts.QUALIFIE.toString(), jsonDossier.getStatuts().get(1).getId());
		assertEquals(EnumStatuts.INCOMPLET.toString(), jsonDossier.getStatuts().get(2).getId());
		assertEquals(EnumStatuts.INSTRUCTION.toString(), jsonDossier.getStatuts().get(3).getId());
		assertEquals(EnumStatuts.COMPLET.toString(), jsonDossier.getStatuts().get(4).getId());
		assertFalse(jsonDossier.getMessages().isEmpty());
		assertEquals(1, jsonDossier.getMessages().size());
	}
	
	@Test
	public void declarerCompletDeposantInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.deposantAccessToken).expect().statusCode(403).when()
				.post("/{id}"+DossiersController.DECLARER_COMPLET_URI, this.dossier.identity().toString());
	}
	
	@Test
	public void lancerConsultationsInstructeurTest() throws Exception {     
        this.statutService.qualifier(this.dossier);        
        this.statutService.declarerIncomplet(this.dossier, this.instructeur, "Incomplet!");
        this.statutService.instruire(this.dossier);      
        this.statutService.declarerComplet(this.dossier);         
		this.dossier = this.dossierRepository.save(this.dossier);
		JsonDossier jsonDossier = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.instructeurAccessToken).expect().statusCode(200).when()
				.post("/{id}"+DossiersController.LANCER_CONSULTATIONS_URI, this.dossier.identity().toString()).then().assertThat().and().extract().jsonPath()
				.getObject("", JsonDossier.class);
		assertNotNull(jsonDossier);
		assertEquals(this.dossier.identity().toString(), jsonDossier.getId());
		assertEquals(EnumStatuts.CONSULTATIONS.toString(), jsonDossier.getStatutActuel().getId());
		assertFalse(jsonDossier.getStatuts().isEmpty());
		assertEquals(6, jsonDossier.getStatuts().size());
		assertEquals(EnumStatuts.DEPOSE.toString(), jsonDossier.getStatuts().get(0).getId());
		assertEquals(EnumStatuts.QUALIFIE.toString(), jsonDossier.getStatuts().get(1).getId());
		assertEquals(EnumStatuts.INCOMPLET.toString(), jsonDossier.getStatuts().get(2).getId());
		assertEquals(EnumStatuts.INSTRUCTION.toString(), jsonDossier.getStatuts().get(3).getId());
		assertEquals(EnumStatuts.COMPLET.toString(), jsonDossier.getStatuts().get(4).getId());
		assertEquals(EnumStatuts.CONSULTATIONS.toString(), jsonDossier.getStatuts().get(5).getId());
		assertFalse(jsonDossier.getMessages().isEmpty());
		assertEquals(1, jsonDossier.getMessages().size());
	}
	
	@Test
	public void lancerConsultationsDeposantInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.deposantAccessToken).expect().statusCode(403).when()
				.post("/{id}"+DossiersController.LANCER_CONSULTATIONS_URI, this.dossier.identity().toString());
	}

	@Test
	public void prendreDecisionMairieTest() throws Exception {
        this.statutService.qualifier(this.dossier);        
        this.statutService.declarerIncomplet(this.dossier, this.instructeur, "Incomplet!");
        this.statutService.instruire(this.dossier);      
        this.statutService.declarerComplet(this.dossier);      
        this.statutService.lancerConsultations(this.dossier);         
		this.dossier = this.dossierRepository.save(this.dossier);
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.mairieAccessToken).multiPart("file", this.dp1).expect().statusCode(200).when()
				.post("/{id}"+DossiersController.PRENDRE_DECISION_URI, this.dossier.identity().toString());
		Optional<Dossier> dossierLu = this.dossierRepository.findById(dossier.identity().toString());
		assertTrue(dossierLu.isPresent());
		assertNotNull(dossierLu.get().pieceJointes());
		assertEquals(EnumStatuts.DECISION, dossierLu.get().statutActuel().get().type().identity());
		assertNotNull(dossierLu.get().decision());
	}
	@Test
	public void prendreDecisionDeposantInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.deposantAccessToken).multiPart("file", this.dp1).expect().statusCode(403).when()
				.post("/{id}"+DossiersController.PRENDRE_DECISION_URI, this.dossier.identity().toString());
	}

	@Test
	public void ajouterMessageInstructeurTest() throws Exception {       
        this.statutService.qualifier(this.dossier);
		this.dossier = this.dossierRepository.save(this.dossier);
		String message = "Le dossier est incomplet car le plan de masse est illisible";
		JsonDossier jsonDossier = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.instructeurAccessToken).contentType(ContentType.JSON).body(message).expect().statusCode(200).when()
				.post("/{id}"+DossiersController.MESSAGES_URI, this.dossier.identity().toString()).then().assertThat().and().extract().jsonPath()
				.getObject("", JsonDossier.class);
		assertNotNull(jsonDossier);
		assertEquals(this.dossier.identity().toString(), jsonDossier.getId());
		assertEquals(EnumStatuts.QUALIFIE.toString(), jsonDossier.getStatutActuel().getId());
		assertFalse(jsonDossier.getStatuts().isEmpty());
		assertEquals(2, jsonDossier.getStatuts().size());
		assertEquals(EnumStatuts.DEPOSE.toString(), jsonDossier.getStatuts().get(0).getId());
		assertEquals(EnumStatuts.QUALIFIE.toString(), jsonDossier.getStatuts().get(1).getId());
		assertFalse(jsonDossier.getMessages().isEmpty());
		assertEquals(1, jsonDossier.getMessages().size());
		assertEquals(message, jsonDossier.getMessages().get(0).getContenu());
		assertEquals(this.instructeur.identity().toString(), jsonDossier.getMessages().get(0).getAuteur().getId());
		assertEquals(this.instructeur.email(), jsonDossier.getMessages().get(0).getAuteur().getEmail());
	}

	@Test
	public void ajouterMessageDeposantTest() throws Exception {       
        this.statutService.qualifier(this.dossier);
		this.dossier = this.dossierRepository.save(this.dossier);
		String message = "Le dossier est incomplet car le plan de masse est illisible";
		JsonDossier jsonDossier = given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.deposantAccessToken).contentType(ContentType.JSON).body(message).expect().statusCode(200).when()
				.post("/{id}"+DossiersController.MESSAGES_URI, this.dossier.identity().toString()).then().assertThat().and().extract().jsonPath()
				.getObject("", JsonDossier.class);
		assertNotNull(jsonDossier);
		assertEquals(this.dossier.identity().toString(), jsonDossier.getId());
		assertEquals(EnumStatuts.QUALIFIE.toString(), jsonDossier.getStatutActuel().getId());
		assertFalse(jsonDossier.getStatuts().isEmpty());
		assertEquals(2, jsonDossier.getStatuts().size());
		assertEquals(EnumStatuts.DEPOSE.toString(), jsonDossier.getStatuts().get(0).getId());
		assertEquals(EnumStatuts.QUALIFIE.toString(), jsonDossier.getStatuts().get(1).getId());
		assertFalse(jsonDossier.getMessages().isEmpty());
		assertEquals(1, jsonDossier.getMessages().size());
		assertEquals(message, jsonDossier.getMessages().get(0).getContenu());
		assertEquals(this.deposantBeta.identity().toString(), jsonDossier.getMessages().get(0).getAuteur().getId());
		assertEquals(this.deposantBeta.email(), jsonDossier.getMessages().get(0).getAuteur().getEmail());
	}

	@Test
	public void ajouterMessageMairieInterditTest() throws Exception {
		Map<String, String> request = new HashMap<>();
    	request.put("message", "Le dossier est incomplet car le plan de masse est illisible");
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.mairieAccessToken).body(request).expect().statusCode(403).when()
				.post("/{id}"+DossiersController.MESSAGES_URI, this.dossier.identity().toString());
	}

	@Test
	public void supprimerDeposantTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.deposantAccessToken).expect().statusCode(200).when()
				.delete("/{id}", this.dossier.identity().toString());
		assertTrue(this.dossierRepository.findById(this.dossier.identity().toString()).isEmpty());
	}

	@Test
	public void supprimerDeposantAutreDossierInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.deposantAccessToken).expect().statusCode(403).when()
				.delete("/{id}", this.dossier.identity().toString());
	}

	@Test
	public void supprimerInstructeurInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.instructeurAccessToken).expect().statusCode(403).when()
				.delete("/{id}", this.dossier.identity().toString());
	}

	@Test
	public void supprimerMairieInterditTest() throws Exception {
		given().port(this.serverPort).basePath(DossiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.mairieAccessToken).expect().statusCode(403).when()
				.delete("/{id}", this.dossier.identity().toString());
	}
}