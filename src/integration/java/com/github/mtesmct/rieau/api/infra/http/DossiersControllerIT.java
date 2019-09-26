package com.github.mtesmct.rieau.api.infra.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.github.mtesmct.rieau.api.domain.entities.dossiers.Dossier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.Fichier;
import com.github.mtesmct.rieau.api.domain.entities.dossiers.TypesDossier;
import com.github.mtesmct.rieau.api.domain.entities.personnes.Personne;
import com.github.mtesmct.rieau.api.domain.factories.DossierFactory;
import com.github.mtesmct.rieau.api.domain.factories.FichierFactory;
import com.github.mtesmct.rieau.api.domain.repositories.DossierRepository;
import com.github.mtesmct.rieau.api.domain.services.FichierService;
import com.github.mtesmct.rieau.api.infra.application.auth.WithDeposantAndBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.auth.WithInstructeurNonBetaDetails;
import com.github.mtesmct.rieau.api.infra.application.dossiers.TxImporterCerfaService;
import com.github.mtesmct.rieau.api.infra.date.DateConverter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.client.registration.ClientRegistrationException;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.test.TestsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WithDeposantAndBetaDetails
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Slf4j
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
	@MockBean
	private TxImporterCerfaService mockImporterCerfaService;

	private Dossier dossier;

	@Autowired
	@Qualifier("deposantBeta")
	private Personne deposantBeta;

	private static String rootUrl = "http://localhost:5000";
	private Fichier fichier;
	private File file;

	@BeforeAll
	public static void setup() throws Exception {
		/**
		 * FluentTestsHelper.importTestRealm() has still a bug (NullPointerException)
		 * caused by keycloak object that is not instantiated with a
		 * getKeycloakInstance() call
		 */
		TestsHelper.baseUrl = rootUrl;
		TestsHelper.keycloakBaseUrl = "http://localhost:8080/auth";
		TestsHelper.testRealm = "rieau";
		TestsHelper.importTestRealm("admin", "admin", "/realm-rieau-test.json");
		TestsHelper.createDirectGrantClient();
	}

	@BeforeEach
	public void initData() throws Exception {
		file = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
		fichier = this.fichierFactory.creer(file, "application/pdf");
		fichierService.save(fichier);
		dossier = dossierFactory.creer(deposantBeta, TypesDossier.DP);
		dossier.ajouterCerfa(fichier.identity());
		dossier = dossierRepository.save(dossier);
		assertNotNull(dossier);
		assertNotNull(dossier.identity());
		assertNotNull(dossier.deposant());
		assertNotNull(dossier.type());
		assertEquals(dossier.deposant().identity(), deposantBeta.identity());
		fichier.fermer();
	}

	@AfterAll
	public static void cleanUp() throws ClientRegistrationException, IOException {
		TestsHelper.deleteRealm("admin", "admin", TestsHelper.testRealm);
	}

	private String obtainRequestingPartyToken(String resourceId, String accessToken) {
		Configuration configuration = new Configuration();
		configuration.setResource(resourceId);
		configuration.setAuthServerUrl(TestsHelper.keycloakBaseUrl);
		configuration.setRealm(TestsHelper.testRealm);
		AuthzClient authzClient = AuthzClient.create(configuration);
		AuthorizationRequest request = new AuthorizationRequest();
		request.addPermission(resourceId);
		return authzClient.authorization(accessToken).authorize(request).getToken();
	}

	private HttpResponse getWithBearer(String uri, String userName, String password, boolean sendRpt, String resourceId)
			throws IOException, URISyntaxException {
		HttpClient client = HttpClientBuilder.create().build();
		URI uriObj = new URIBuilder(rootUrl).build();
		HttpGet request = new HttpGet(uriObj + uri);
		String accessToken = TestsHelper.getToken(userName, password, TestsHelper.testRealm);
		String rpt;
		if (sendRpt) {
			rpt = obtainRequestingPartyToken(resourceId, accessToken);
		} else {
			rpt = accessToken;
		}
		request.addHeader("Authorization", "Bearer " + rpt);
		log.debug("request={}", request);
		return client.execute(request);
	}

	private HttpResponse postWithBearer(String uri, File file, String mimeType, String userName,
			String password, boolean sendRpt, String resourceId) throws IOException, URISyntaxException {
		HttpClient client = HttpClientBuilder.create().build();
		URI uriObj = new URIBuilder(rootUrl).build();
		HttpPost request = new HttpPost(uriObj + uri);
		String accessToken = TestsHelper.getToken(userName, password, TestsHelper.testRealm);
		String rpt;
		if (sendRpt) {
			rpt = obtainRequestingPartyToken(resourceId, accessToken);
		} else {
			rpt = accessToken;
		}
		request.addHeader("Authorization", "Bearer " + rpt);
		log.debug("request={}", request);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addBinaryBody("file", file, ContentType.parse(mimeType), file.getName());
		HttpEntity multipart = builder.build();
		request.setEntity(multipart);
		return client.execute(request);
	}

	private HttpResponse getWithoutBearer(String uri) throws IOException, URISyntaxException {
		HttpClient client = HttpClientBuilder.create().build();
		URI uriObj = new URIBuilder(rootUrl).build();
		HttpGet request = new HttpGet(uriObj + uri);
		log.debug("request={}", request);
		return client.execute(request);
	}

	private HttpResponse postWithoutBearer(String uri, File file, String mimeType)
			throws IOException, URISyntaxException {
		HttpClient client = HttpClientBuilder.create().build();
		URI uriObj = new URIBuilder(rootUrl).build();
		HttpPost request = new HttpPost(uriObj + uri);
		log.debug("request={}", request);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addBinaryBody("file", file, ContentType.parse(mimeType), file.getName());
		HttpEntity multipart = builder.build();
		request.setEntity(multipart);
		return client.execute(request);
	}

	private void assertAccessGranted(HttpResponse response) throws IOException {
		log.debug("response={}", response);
		log.debug("entity={}", EntityUtils.toString(response.getEntity()));
		assertEquals(200, response.getStatusLine().getStatusCode());
	}

	private void assertAccessDenied(HttpResponse response) throws IOException {
		log.debug("response={}", response);
		log.debug("entity={}", EntityUtils.toString(response.getEntity()));
		assertEquals(401, response.getStatusLine().getStatusCode());
	}

	private void assertAccessForbidden(HttpResponse response) throws IOException {
		log.debug("response={}", response);
		log.debug("entity={}", EntityUtils.toString(response.getEntity()));
		assertEquals(403, response.getStatusLine().getStatusCode());
	}

	@Test
	public void listerTest() throws Exception {
		HttpResponse response = getWithBearer(DossiersController.ROOT_URI, WithDeposantAndBetaDetails.ID,
				WithDeposantAndBetaDetails.ID, false, "Default Resource");
		assertAccessGranted(response);
	}

	@Test
	public void listerInterditTest() throws Exception {
		HttpResponse response = getWithoutBearer(DossiersController.ROOT_URI);
		assertAccessDenied(response);
	}

	@Test
	public void consulterTest() throws Exception {
		HttpResponse response = getWithBearer(DossiersController.ROOT_URI + "/" + dossier.identity().toString(),
				WithDeposantAndBetaDetails.ID, WithDeposantAndBetaDetails.ID, false, "Default Resource");
		assertAccessGranted(response);
	}

	@Test
	public void consulterInterditTest() throws Exception {
		HttpResponse response = getWithoutBearer(DossiersController.ROOT_URI + "/" + dossier.identity().toString());
		assertAccessDenied(response);
		response = getWithBearer(DossiersController.ROOT_URI + "/" + dossier.identity().toString(),
				WithInstructeurNonBetaDetails.ID, WithInstructeurNonBetaDetails.ID, false, "Default Resource");
		assertAccessForbidden(response);
	}

	@Test
	public void ajouterCerfaTest() throws Exception {
		FileInputStream fis = new FileInputStream(file);
		HttpResponse response = postWithBearer(DossiersController.ROOT_URI, file, "application/pdf",
				WithDeposantAndBetaDetails.ID, WithDeposantAndBetaDetails.ID, false, "Default Resource");
		assertAccessGranted(response);
		fis.close();
	}

	@Test
	public void ajouterCerfaInterditTest() throws Exception {
		HttpResponse response = postWithoutBearer(DossiersController.ROOT_URI, file, "application/pdf");
		assertAccessDenied(response);
		response = postWithBearer(DossiersController.ROOT_URI, file, "application/pdf",
				WithInstructeurNonBetaDetails.ID, WithInstructeurNonBetaDetails.ID, false, "Default Resource");
		assertAccessForbidden(response);
	}
}