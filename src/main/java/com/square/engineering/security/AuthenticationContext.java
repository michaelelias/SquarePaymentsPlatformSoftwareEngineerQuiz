package com.square.engineering.security;

import org.apache.log4j.Logger;


public class AuthenticationContext {

	private static Logger LOG = Logger.getLogger(AuthenticationContext.class);
	
	private static final ThreadLocal<Credentials> securityThreadLocal = new ThreadLocal<Credentials>();

	private static AuthenticationContext instance = null;
	
	private static AuthenticationException exception;

	private AuthenticationContext() {
	} // Singleton
	
	public static AuthenticationContext get() {
		if (instance == null) {
			instance = new AuthenticationContext();
		}
		return instance;
	}
	
	public Credentials getCredentials() {
		return securityThreadLocal.get();
	}
	
	public void setCredentials(Credentials credentials) {
		securityThreadLocal.set(credentials);
		exception = null;
		LOG.info("Set " + credentials.toString());
	}
	
	public void setException(AuthenticationException e){
		exception = e;
	}
	
	public static AuthenticationException getException() {
		return exception;
	}

	public boolean isAuthenticated(){
		return exception == null;
	}
}
