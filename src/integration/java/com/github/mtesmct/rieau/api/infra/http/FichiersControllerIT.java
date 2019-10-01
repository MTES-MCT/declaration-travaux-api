package com.github.mtesmct.rieau.api.infra.http;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.infra.application.auth.WithAutreDeposantBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithInstructeurNonBetaDetails;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;
import com.github.mtesmct.rieau.api.infra.http.fichiers.FichiersController;

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
public class FichiersControllerIT {

	@Autowired
	@Qualifier("dateTimeConverter")
	private DateConverter dateConverter;
	@Autowired
	private FichierService fichierService;
	@Autowired
	private FichierFactory fichierFactory;
    @Autowired
    private DossierFactory dossierFactory;
    @Autowired
    private DossierRepository dossierRepository;

	@Autowired
	@Qualifier("deposantBeta")
	private Personne deposantBeta;
	private Fichier fichier;
	private File cerfa;

	@Autowired
	private KeycloakTestsHelper keycloakTestsHelper;
	@LocalServerPort
	private int serverPort;
	private String accessToken;
	private String invalidToken = "invalid-token";

	@BeforeEach
	public void initData() throws Exception {
		cerfa = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
		fichier = this.fichierFactory.creer(cerfa, "application/pdf");
		fichierService.save(fichier);
        Dossier dp = this.dossierFactory.creer(this.deposantBeta, TypesDossier.DP);
        dp.ajouterCerfa(fichier.identity());
        dp = this.dossierRepository.save(dp);
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		this.accessToken = this.keycloakTestsHelper.getAccessToken(WithDeposantBetaDetails.ID,
				WithDeposantBetaDetails.ID);
	}

	@Test
	public void lireTest() throws Exception {
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
	public void lireInterditTest() throws Exception {
		given().port(this.serverPort).basePath(FichiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.keycloakTestsHelper.getAccessToken(WithInstructeurNonBetaDetails.ID,
						WithInstructeurNonBetaDetails.ID))
				.multiPart("file", this.cerfa).expect().statusCode(403).when()
				.get("/{id}", this.fichier.identity().toString());
	}

	@Test
	public void lireNonProprietaireTest() throws Exception {
		given().port(this.serverPort).basePath(FichiersController.ROOT_URI).auth().preemptive()
				.oauth2(this.keycloakTestsHelper.getAccessToken(WithAutreDeposantBetaDetails.ID,
				WithAutreDeposantBetaDetails.ID))
				.multiPart("file", this.cerfa).expect().statusCode(403).when()
				.get("/{id}", this.fichier.identity().toString());
	}
}