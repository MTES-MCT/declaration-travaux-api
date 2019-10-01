package com.github.mtesmct.rieau.api.infra.http;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.keycloak.OAuth2Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeycloakTestsHelper {

	@Value("${keycloak.auth-server-url}")
	private String authServerUrl;
	@Value("${keycloak.ssl-required}")
	private String sslRequired;
	@Value("${keycloak.credentials.secret}")
	private String credentialsSecret;
	@Value("${keycloak.resource}")
	private String resource;
	@Value("${keycloak.realm}")
	private String realm;
	@Value("${keycloak.bearer-only}")
	private boolean bearerOnly;
	@Value("${keycloak.use-resource-role-mappings}")
	private boolean useResourceRoleMappings;

	public String getAccessToken(String userName, String userPassword)
			throws URISyntaxException, ClientProtocolException, IOException, JSONException {
		String accessToken = given().formParam("username", userName).formParam("password", userPassword)
				.formParam("client_id", this.resource).formParam("client_secret", this.credentialsSecret)
				.formParam("grant_type", OAuth2Constants.PASSWORD)
				.post("/auth/realms/{realm}/protocol/openid-connect/token", this.realm).then().assertThat()
				.statusCode(200).extract().jsonPath().getString("access_token");
		assertNotNull(accessToken);
		return accessToken;
	}

	public boolean isEqual(InputStream i1, InputStream i2) throws IOException {
		byte[] buf1 = new byte[64 * 1024];
		byte[] buf2 = new byte[64 * 1024];
		boolean equal = false;
		try {
			DataInputStream d2 = new DataInputStream(i2);
			int len;
			while ((len = i1.read(buf1)) > 0) {
				d2.readFully(buf2, 0, len);
				for (int i = 0; i < len; i++) {
					if (buf1[i] != buf2[i]) {
						equal = false;
						d2.close();
						return equal;
					}
				}
			}
			equal = d2.read() < 0;
			d2.close();
		} catch (EOFException ioe) {
		} finally {
			i1.close();
			i2.close();
		}
		return equal;
	}
}