package com.github.mtesmct.rieau.api.infra.http;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.*;
import com.github.mtesmct.rieau.api.domain.entities.personnes.User;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.factories.ProjetFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.infra.application.auth.WithAutreDeposantBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithInstructeurNonBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithMairieBetaDetails;
import com.github.mtesmct.rieau.api.infra.http.fichiers.FichiersController;
import io.restassured.RestAssured;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FichiersControllerIT {

	@Autowired
	private FichierService fichierService;
	@Autowired
	private FichierFactory fichierFactory;
	@Autowired
	private DossierFactory dossierFactory;
	@Autowired
	private ProjetFactory projetFactory;
	@Autowired
	private DossierRepository dossierRepository;

	@Autowired
	@Qualifier("deposantBeta")
	private User deposantBeta;
	private Fichier fichier;
	private File cerfa;

	@Autowired
	private KeycloakTestsHelper keycloakTestsHelper;
	@LocalServerPort
	private int serverPort;
	private String accessToken;
	private String mairieAccessToken;
	private String instructeurAccessToken;
	private String invalidToken = "invalid-token";

	@BeforeEach
	@Transactional
	public void initData() throws Exception {
		cerfa = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
		fichier = this.fichierFactory.creer(cerfa, "application/pdf");
		fichierService.save(fichier);
		Projet projet = this.projetFactory.creer("1", "rue des Lilas", "ZA des Fleurs", "44100", "BP 44", "Cedex 01",
				new ParcelleCadastrale("0", "1", "2"), true, true);
		Dossier dp = this.dossierFactory.creer(this.deposantBeta, EnumTypes.DPMI, projet, fichier.identity());
		dp = this.dossierRepository.save(dp);
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		this.accessToken = this.keycloakTestsHelper.getAccessToken(WithDeposantBetaDetails.ID,
				WithDeposantBetaDetails.ID);
		this.mairieAccessToken = this.keycloakTestsHelper.getAccessToken(WithMairieBetaDetails.ID,
				WithMairieBetaDetails.ID);
		this.instructeurAccessToken = this.keycloakTestsHelper.getAccessToken(WithInstructeurNonBetaDetails.ID,
				WithInstructeurNonBetaDetails.ID);
	}

	@Test
	public void lireDeposantTest() throws Exception {
		InputStream inputStream = given().port(this.serverPort).basePath(FichiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.accessToken).multiPart("file", this.cerfa).expect().statusCode(200).when()
				.get("/{id}", this.fichier.identity().toString()).asInputStream();
		assertTrue(this.keycloakTestsHelper.isEqual(new FileInputStream(this.cerfa), inputStream));
	}

	@Test
	public void lireNonAuthentifieTest() throws Exception {
		given().port(this.serverPort).basePath(FichiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.invalidToken).multiPart("file", this.cerfa).expect().statusCode(401).when()
				.get("/{id}", this.fichier.identity().toString());
	}

	@Test
	public void lireDeposantNonProprietaireTest() throws Exception {
		given().port(this.serverPort).basePath(FichiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.keycloakTestsHelper.getAccessToken(WithAutreDeposantBetaDetails.ID,
						WithAutreDeposantBetaDetails.ID))
				.multiPart("file", this.cerfa).expect().statusCode(403).when()
				.get("/{id}", this.fichier.identity().toString());
	}

	@Test
	public void lireMairieTest() throws Exception {
		InputStream inputStream = given().port(this.serverPort).basePath(FichiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.mairieAccessToken).multiPart("file", this.cerfa).expect().statusCode(200)
				.when().get("/{id}", this.fichier.identity().toString()).asInputStream();
		assertTrue(this.keycloakTestsHelper.isEqual(new FileInputStream(this.cerfa), inputStream));
	}

	@Test
	public void lireMairieNonLocaliseeTest() throws Exception {
		File otherFile = new File("src/test/fixtures/dummy.pdf");
		Fichier otherFichier = this.fichierFactory.creer(otherFile, "application/pdf");
		this.fichierService.save(otherFichier);
		Projet projet = this.projetFactory.creer("2", "rue des Fleurs", "ZI", "44500", "BP 1", "Cedex 02",
				new ParcelleCadastrale("1", "2", "3"), true, true);
		Dossier dossier = this.dossierFactory.creer(this.deposantBeta, EnumTypes.DPMI, projet, otherFichier.identity());
		dossier = this.dossierRepository.save(dossier);
		given().port(this.serverPort).basePath(FichiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.mairieAccessToken).multiPart("file", otherFile).expect().statusCode(403).when()
				.get("/{id}", otherFichier.identity().toString());
	}

	@Test
	public void lireInstructeurTest() throws Exception {
		InputStream inputStream = given().port(this.serverPort).basePath(FichiersController.ROOT_URI).auth()
				.preemptive().oauth2(this.instructeurAccessToken).multiPart("file", this.cerfa).expect().statusCode(200)
				.when().get("/{id}", this.fichier.identity().toString()).asInputStream();
		assertTrue(this.keycloakTestsHelper.isEqual(new FileInputStream(this.cerfa), inputStream));
	}

	@Test
	public void lireInstructeurNonLocaliseTest() throws Exception {
		File otherFile = new File("src/test/fixtures/dummy.pdf");
		Fichier otherFichier = this.fichierFactory.creer(otherFile, "application/pdf");
		this.fichierService.save(otherFichier);
		Projet projet = this.projetFactory.creer("2", "rue des Fleurs", "ZI", "44500", "BP 1", "Cedex 02",
				new ParcelleCadastrale("1", "2", "3"), true, true);
		Dossier dossier = this.dossierFactory.creer(this.deposantBeta, EnumTypes.DPMI, projet, otherFichier.identity());
		dossier = this.dossierRepository.save(dossier);
		given().port(this.serverPort).basePath(FichiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.instructeurAccessToken).multiPart("file", otherFile).expect().statusCode(403).when()
				.get("/{id}", otherFichier.identity().toString());
	}
}