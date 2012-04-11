package com.square.engineering.security;

import java.io.InputStream;
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
			InputStream is = CredentialsStore.class.getResourceAsStream(FILE);
			Properties properties = new Properties();
			properties.load(is);
			
			String[] merchants = properties.getProperty("merchants").split(",");
			for(String merchant : merchants){
				loadMerchantDetails(merchant, properties);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void loadMerchantDetails(String merchant, Properties properties){
		String name = properties.getProperty("merchant." + merchant + ".name");
		String oauth = properties.getProperty("merchant." + merchant + ".oauth.token");
		String username = properties.getProperty("merchant." + merchant + ".basic.username");
		String password = properties.getProperty("merchant." + merchant + ".basic.password");
		
		Credentials creds = new Credentials(name, oauth, username, password);
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
