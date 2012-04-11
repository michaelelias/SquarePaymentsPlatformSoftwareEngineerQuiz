package com.square.engineering.client;

import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.BeforeClass;
import org.junit.Test;

import com.square.engineering.APIException;
import com.square.engineering.Location;

public class RESTLocationApiBeanTestUnautomated {

	private static final String SERVER_URL = "http://localhost:8880";

	private static RESTLocationApiClient client;

	private AuthenticationStrategy noAuthenticationStrategy = new NoAuthenticationStrategy();
	private AuthenticationStrategy traderJoesOAuthStrategy = new OAuthAuthenticationStrategy("123456789");
	private AuthenticationStrategy traderJoesBasicStrategy = new BasicAuthenticationStrategy("joe", "trader");

	@BeforeClass
	public static void setupClass() {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		client = new RESTLocationApiClient(httpClient, SERVER_URL);
	}

	@Test
	public void testGetExistingLocationWithNoCredentials() {
		applyAuthenticationStartegy(noAuthenticationStrategy);
		try {
			client.get("123");
			Assert.fail("Should throw unauthorized exception");
		} catch (APIException e) {
			Assert.assertEquals(e.getHttpStatusCode(), Status.UNAUTHORIZED.getStatusCode());
		}
	}

	@Test
	public void testGetExistingLocationWithTraderJoesOAuthAuthentication() throws APIException {
		applyAuthenticationStartegy(traderJoesOAuthStrategy);
		Location location = client.get("123");
		Assert.assertNotNull(location);
		Assert.assertEquals("123", location.getId());
	}

	@Test
	public void testGetExistingLocationWithTraderJoesBasicAuthentication() throws APIException {
		applyAuthenticationStartegy(traderJoesBasicStrategy);
		Location location = client.get("123");
		Assert.assertNotNull(location);
		Assert.assertEquals("123", location.getId());
	}

	@Test
	public void testGetNonExistingLocationWithTraderJoesOAuthAuthentication() throws APIException {
		applyAuthenticationStartegy(traderJoesOAuthStrategy);
		Location location = client.get("zzzzzzzz");
		Assert.assertNull(location);
	}

	private void applyAuthenticationStartegy(AuthenticationStrategy authStrategy) {
		client.setAuthStrategy(authStrategy);
	}

}
