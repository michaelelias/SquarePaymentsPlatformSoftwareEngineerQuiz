package com.square.engineering.security;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;


public class CredentialsStore {

	private static final Logger LOG = Logger.getLogger(CredentialsStore.class);
	
	private static final String FILE = "credentials.properties";

	private List<Credentials> credentials = new LinkedList<Credentials>();
	
	public CredentialsStore(){
		loadCredentials();
	}
	
	private void loadCredentials() {
		try {
			Properties properties = new Properties();
			properties.load(CredentialsStore.class.getResourceAsStream(FILE));
			
			String[] merchantIds = properties.getProperty("merchants").split(",");
			for(String merchantId : merchantIds){
				loadMerchantDetails(merchantId, properties);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void loadMerchantDetails(String merchantId, Properties properties){
		String name = properties.getProperty("merchant." + merchantId + ".name");
		String oauth = properties.getProperty("merchant." + merchantId + ".oauth.token");
		String username = properties.getProperty("merchant." + merchantId + ".basic.username");
		String password = properties.getProperty("merchant." + merchantId + ".basic.password");
		
		Credentials creds = new Credentials(merchantId, name, oauth, username, password);
		credentials.add(creds);
		LOG.info("Loaded " + creds.toString());
	}

	public Credentials authenticateByToken(String token) throws AuthenticationException {
		for(Credentials creds : credentials){
			if(creds.getOauthToken().equals(token)){
				return creds;
			}
		}
		throw new AuthenticationException("Token " + token + " does not match known credentials. Authentication Failed");
	}

	public Credentials authenticateByCredentials(String username, String password) throws AuthenticationException {
		for(Credentials creds : credentials){
			if(creds.getBasicUsername().equals(username) && creds.getBasicPassword().equals(password)){
				return creds;
			}
		}
		throw new AuthenticationException("Username " + username + " and provided password do not match known credentials. Authentication Failed");
	}
	
	
	
}
