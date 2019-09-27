package com.github.mtesmct.rieau.api.infra.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	private File cerfa;
	private File dp1;
	private Map<String, String> params;

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
		cerfa = new File("src/test/fixtures/cerfa_13703_DPMI.pdf");
		fichier = this.fichierFactory.creer(cerfa, "application/pdf");
		fichierService.save(fichier);
		dossier = dossierFactory.creer(deposantBeta, TypesDossier.DP);
		dossier.ajouterCerfa(fichier.identity());
		dossier = dossierRepository.save(dossier);
		assertNotNull(dossier);
		assertNotNull(dossier.identity());
		assertNotNull(dossier.deposant());
		assertNotNull(dossier.type());
		assertEquals(dossier.deposant().identity(), deposantBeta.identity());
		params = new HashMap<String, String>();
		dp1 = new File("src/test/fixtures/dummy.pdf");
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

	private URIBuilder uriBuilder(String uri, Map<String, String> params) throws URISyntaxException {
		URIBuilder uriBuilder = new URIBuilder(rootUrl);
		uriBuilder.setPath(uri);
		Set<Entry<String, String>> entries = params.entrySet();
		Iterator<Entry<String, String>> iter = entries.iterator();
		while (iter.hasNext()) {
			Entry<String, String> e = iter.next();
			uriBuilder.setParameter(e.getKey(), e.getValue());
		}
		return uriBuilder;
	}

	private HttpResponse getWithBearer(String uri, Map<String, String> params, String userName, String password,
			boolean sendRpt, String resourceId) throws IOException, URISyntaxException {
		HttpClient client = HttpClientBuilder.create().build();
		URIBuilder uriBuilder = uriBuilder(uri, params);
		HttpGet request = new HttpGet(uriBuilder.build());
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

	private HttpResponse postWithBearer(String uri, Map<String, String> params, File file, String mimeType,
			String userName, String password, boolean sendRpt, String resourceId)
			throws IOException, URISyntaxException {
		HttpClient client = HttpClientBuilder.create().build();
		URIBuilder uriBuilder = uriBuilder(uri, params);
		HttpPost request = new HttpPost(uriBuilder.build());
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

	private HttpResponse getWithoutBearer(String uri, Map<String, String> params)
			throws IOException, URISyntaxException {
		HttpClient client = HttpClientBuilder.create().build();
		URIBuilder uriBuilder = uriBuilder(uri, params);
		HttpGet request = new HttpGet(uriBuilder.build());
		log.debug("request={}", request);
		return client.execute(request);
	}

	private HttpResponse postWithoutBearer(String uri, Map<String, String> params, File file, String mimeType)
			throws IOException, URISyntaxException {
		HttpClient client = HttpClientBuilder.create().build();
		URIBuilder uriBuilder = uriBuilder(uri, params);
		HttpPost request = new HttpPost(uriBuilder.build());
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
		HttpResponse response = getWithBearer(DossiersController.ROOT_URI, params, WithDeposantAndBetaDetails.ID,
				WithDeposantAndBetaDetails.ID, false, "Default Resource");
		assertAccessGranted(response);
	}

	@Test
	public void listerInterditTest() throws Exception {
		HttpResponse response = getWithoutBearer(DossiersController.ROOT_URI, params);
		assertAccessDenied(response);
	}

	@Test
	public void consulterTest() throws Exception {
		HttpResponse response = getWithBearer(DossiersController.ROOT_URI + "/" + dossier.identity().toString(), params,
				WithDeposantAndBetaDetails.ID, WithDeposantAndBetaDetails.ID, false, "Default Resource");
		assertAccessGranted(response);
	}

	@Test
	public void consulterInterditTest() throws Exception {
		HttpResponse response = getWithoutBearer(DossiersController.ROOT_URI + "/" + dossier.identity().toString(),
				params);
		assertAccessDenied(response);
		response = getWithBearer(DossiersController.ROOT_URI + "/" + dossier.identity().toString(), params,
				WithInstructeurNonBetaDetails.ID, WithInstructeurNonBetaDetails.ID, false, "Default Resource");
		assertAccessForbidden(response);
	}

	@Test
	public void ajouterCerfaTest() throws Exception {
		HttpResponse response = postWithBearer(DossiersController.ROOT_URI, params, cerfa, "application/pdf",
				WithDeposantAndBetaDetails.ID, WithDeposantAndBetaDetails.ID, false, "Default Resource");
		assertAccessGranted(response);
		Optional<Dossier> dossierLu = this.dossierRepository.findById(dossier.identity().toString());
		assertTrue(dossierLu.isPresent());
		assertNotNull(dossierLu.get().cerfa());
		assertNotNull(dossierLu.get().cerfa().code());
		assertEquals(TypesDossier.DP, dossierLu.get().cerfa().code().type());
	}

	@Test
	public void ajouterCerfaInterditTest() throws Exception {
		HttpResponse response = postWithoutBearer(DossiersController.ROOT_URI, params, cerfa, "application/pdf");
		assertAccessDenied(response);
		response = postWithBearer(DossiersController.ROOT_URI, params, cerfa, "application/pdf",
				WithInstructeurNonBetaDetails.ID, WithInstructeurNonBetaDetails.ID, false, "Default Resource");
		assertAccessForbidden(response);
	}

	@Test
	public void ajouterPieceJointeTest() throws Exception {
		HttpResponse response = postWithBearer(
				DossiersController.ROOT_URI + "/" + dossier.identity().toString() + "/piecesjointes/1", params, dp1,
				"application/pdf", WithDeposantAndBetaDetails.ID, WithDeposantAndBetaDetails.ID, false,
				"Default Resource");
		assertAccessGranted(response);
		Optional<Dossier> dossierLu = this.dossierRepository.findById(dossier.identity().toString());
		assertTrue(dossierLu.isPresent());
		assertNotNull(dossierLu.get().pieceJointes());
		assertFalse(dossierLu.get().pieceJointes().isEmpty());
		assertEquals(1, dossierLu.get().pieceJointes().size());
		assertEquals("1", dossierLu.get().pieceJointes().get(0).code().numero());
		assertEquals(TypesDossier.DP, dossierLu.get().pieceJointes().get(0).code().type());
		
	}

	@Test
	public void ajouterPieceJointeInterditTest() throws Exception {
		HttpResponse response = postWithoutBearer(
				DossiersController.ROOT_URI + "/" + dossier.identity().toString() + "/piecesjointes/1", params, dp1,
				"application/pdf");
		assertAccessDenied(response);
		response = postWithBearer(DossiersController.ROOT_URI, params, dp1, "application/pdf",
				WithInstructeurNonBetaDetails.ID, WithInstructeurNonBetaDetails.ID, false, "Default Resource");
		assertAccessForbidden(response);
	}
}