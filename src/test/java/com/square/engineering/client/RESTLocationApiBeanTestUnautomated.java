package com.square.engineering.client;

import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.square.engineering.location.Location;
import com.square.engineering.rest.APIException;

public class RESTLocationApiBeanTestUnautomated {

	private static final String SERVER_URL = "http://localhost:8880";

	private static RESTLocationApiClient client;

	@BeforeClass
	public static void setupClass() {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		client = new RESTLocationApiClient(httpClient, SERVER_URL);
	}

	@Test
	public void testGetExistingLocationWithNoCredentials() {
		applyAuthenticationStartegy(Authorizations.noAuthenticationStrategy);
		try {
			client.get("123");
			Assert.fail("Should throw unauthorized exception");
		} catch (APIException e) {
			Assert.assertEquals(e.getHttpStatusCode(), Status.UNAUTHORIZED.getStatusCode());
		}
	}

	@Test
	public void testGetExistingLocationWithTraderJoesOAuthAuthentication() throws APIException {
		applyAuthenticationStartegy(Authorizations.traderJoesOAuthStrategy);
		Location location = client.get("123");
		Assert.assertNotNull(location);
		Assert.assertEquals("123", location.getId());
	}

	@Test
	public void testGetExistingLocationWithTraderJoesBasicAuthentication() throws APIException {
		applyAuthenticationStartegy(Authorizations.traderJoesBasicStrategy);
		Location location = client.get("123");
		Assert.assertNotNull(location);
		Assert.assertEquals("123", location.getId());
	}
	
	@Test
	public void testGetExistingLocationWithWrongBasicAuthentication() throws APIException {
		applyAuthenticationStartegy(new BasicAuthenticationStrategy("somedude", "somepassw"));
		try {
			client.get("123");
			Assert.fail("Should throw not authorized exception");
		} catch (APIException e){
			Assert.assertEquals(e.getHttpStatusCode(), Status.UNAUTHORIZED.getStatusCode());
		}
		
	}

	@Test
	public void testGetNonExistingLocationWithTraderJoesOAuthAuthentication() throws APIException {
		applyAuthenticationStartegy(Authorizations.traderJoesOAuthStrategy);
		try {
			client.get("zzzzzzzz");
			Assert.fail("Should throw not found exception");
		} catch (APIException e) {
			Assert.assertEquals(e.getHttpStatusCode(), Status.NOT_FOUND.getStatusCode());
		}
	}
	
	@Test
	public void testGetExistingLocationNotOwnedByTraderJoesWithTraderJoesOAuthAuthentication() throws APIException {
		applyAuthenticationStartegy(Authorizations.traderJoesOAuthStrategy);
		try {
			client.get("321");
			Assert.fail("Should throw not found exception");
		} catch (APIException e) {
			Assert.assertEquals(e.getHttpStatusCode(), Status.FORBIDDEN.getStatusCode());
		}
	}
	
	@Test
	public void testUpdateExistingLocationWithTraderJoesCredentials() throws APIException{
		applyAuthenticationStartegy(Authorizations.traderJoesOAuthStrategy);
		Location location = client.get("123");
		String updatedName = location.getName() + "x";
		location.setName(updatedName);
		Location updatedLocation = client.update(location.getId(), location);
		
		Assert.assertEquals(updatedName, updatedLocation.getName());
	}
	
	@Test
	public void testGenerateApiTraffic(){
		TrafficGenerator generator = new TrafficGenerator(client);
		generator.generate(10000, 50);
	}
	

	private void applyAuthenticationStartegy(AuthenticationStrategy authStrategy) {
		client.setAuthStrategy(authStrategy);
	}

}
