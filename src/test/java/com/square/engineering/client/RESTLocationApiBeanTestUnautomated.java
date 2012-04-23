package com.square.engineering.client;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.square.engineering.location.Location;
import com.square.engineering.rest.APIException;

public class RESTLocationApiBeanTestUnautomated {

	// private static final String SERVER_URL = "http://localhost:8880";
	private static final String SERVER_URL = "https://localhost";

	private static RESTLocationApiClient client;

	@BeforeClass
	public static void setupClass() {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		if (SERVER_URL.startsWith("https")) {
			// Fix when using https; used certificate is not valid
			try {
				SSLContext ctx = SSLContext.getInstance("TLS");
				X509TrustManager tm = new X509TrustManager() {
					public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
						
					}
					public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
						
					}
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
				};
				ctx.init(null, new TrustManager[]{tm}, null);
				SSLSocketFactory ssf = new SSLSocketFactory(ctx);
				ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				ClientConnectionManager ccm = httpClient.getConnectionManager();
				SchemeRegistry sr = ccm.getSchemeRegistry();
				sr.register(new Scheme("https", ssf, 443));
				httpClient = new DefaultHttpClient(ccm, httpClient.getParams());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} 
		client = new RESTLocationApiClient(httpClient, SERVER_URL);
	}

	@Test
	public void testGetExistingLocationWithNoCredentials() {
		applyAuthenticationStrategy(Authorizations.noAuthenticationStrategy);
		try {
			client.get("123");
			Assert.fail("Should throw unauthorized exception");
		} catch (APIException e) {
			Assert.assertEquals(e.getHttpStatusCode(), Status.UNAUTHORIZED.getStatusCode());
		}
	}

	@Test
	public void testGetExistingLocationWithTraderJoesOAuthAuthentication() throws APIException {
		applyAuthenticationStrategy(Authorizations.traderJoesOAuthStrategy);
		Location location = client.get("123");
		Assert.assertNotNull(location);
		Assert.assertEquals("123", location.getId());
	}

	@Test
	public void testGetExistingLocationWithTraderJoesBasicAuthentication() throws APIException {
		applyAuthenticationStrategy(Authorizations.traderJoesBasicStrategy);
		Location location = client.get("123");
		Assert.assertNotNull(location);
		Assert.assertEquals("123", location.getId());
	}

	@Test
	public void testGetExistingLocationWithWrongBasicAuthentication() throws APIException {
		applyAuthenticationStrategy(new BasicAuthenticationStrategy("somedude", "somepassw"));
		try {
			client.get("123");
			Assert.fail("Should throw not authorized exception");
		} catch (APIException e) {
			Assert.assertEquals(e.getHttpStatusCode(), Status.UNAUTHORIZED.getStatusCode());
		}

	}

	@Test
	public void testGetNonExistingLocationWithTraderJoesOAuthAuthentication() throws APIException {
		applyAuthenticationStrategy(Authorizations.traderJoesOAuthStrategy);
		try {
			client.get("zzzzzzzz");
			Assert.fail("Should throw not found exception");
		} catch (APIException e) {
			Assert.assertEquals(e.getHttpStatusCode(), Status.NOT_FOUND.getStatusCode());
		}
	}

	@Test
	public void testGetExistingLocationNotOwnedByTraderJoesWithTraderJoesOAuthAuthentication() throws APIException {
		applyAuthenticationStrategy(Authorizations.traderJoesOAuthStrategy);
		try {
			client.get("321");
			Assert.fail("Should throw not found exception");
		} catch (APIException e) {
			Assert.assertEquals(e.getHttpStatusCode(), Status.FORBIDDEN.getStatusCode());
		}
	}

	@Test
	public void testUpdateExistingLocationWithTraderJoesCredentials() throws APIException {
		applyAuthenticationStrategy(Authorizations.traderJoesOAuthStrategy);
		Location location = client.get("123");
		String updatedName = location.getName() + "x";
		location.setName(updatedName);
		Location updatedLocation = client.update(location.getId(), location);

		Assert.assertEquals(updatedName, updatedLocation.getName());
	}

	@Test
	public void testUpdateExistingLocationWithTraderJoesCredentialsWithNoRequestBody() {
		applyAuthenticationStrategy(Authorizations.traderJoesOAuthStrategy);
		try {
			client.update("123", null);
			Assert.fail("Expected BadRequest exception to be thrown");
		} catch (APIException e) {
			Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), e.getHttpStatusCode());
		}
	}

	@Test
	public void testUpdateExistingLocationWithNoCredentials() throws APIException {
		applyAuthenticationStrategy(Authorizations.traderJoesBasicStrategy);
		Location location = client.get("123");
		String updatedName = location.getName() + "x";
		location.setName(updatedName);
		applyAuthenticationStrategy(Authorizations.noAuthenticationStrategy);
		try {
			client.update("123", location);
			Assert.fail("Expected UnAuthorized exception to be thrown");
		} catch (APIException e) {
			Assert.assertEquals(Status.UNAUTHORIZED.getStatusCode(), e.getHttpStatusCode());
		}
	}

	@Test
	public void testDeleteLocationWithNoCredentials() throws APIException {
		applyAuthenticationStrategy(Authorizations.noAuthenticationStrategy);
		try {
			client.delete("123");
			Assert.fail("Expected UnAuthorized exception to be thrown");
		} catch (APIException e) {
			Assert.assertEquals(Status.UNAUTHORIZED.getStatusCode(), e.getHttpStatusCode());
		}
	}

	@Test
	@Ignore
	public void testDeleteLocationWithTraderJoesBasicAuthentication() throws APIException {
		applyAuthenticationStrategy(Authorizations.traderJoesBasicStrategy);
		client.delete("123");
		try {
			client.get("123");
		} catch (APIException e) {
			Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), e.getHttpStatusCode());
		}
	}

	@Test
	public void testGenerateApiTraffic() {
		TrafficGenerator generator = new TrafficGenerator(client);
		generator.generate(10000, 50);
	}

	private void applyAuthenticationStrategy(AuthenticationStrategy authStrategy) {
		client.setAuthStrategy(authStrategy);
	}

}
