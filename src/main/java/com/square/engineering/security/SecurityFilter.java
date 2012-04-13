package com.square.engineering.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;


public class SecurityFilter implements Filter {

	private static Logger LOG = Logger.getLogger(SecurityFilter.class);

	private static CredentialsStore credentialsStore = new CredentialsStore();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			if (hasOAuthCredentials(request)) {
				LOG.info("Has OAuth credentials");
				String token = extractToken(request);
				Credentials credentials = credentialsStore.authenticateByToken(token);
				AuthenticationContext.get().setCredentials(credentials);
			} else if (hasBasicCredentials(request)) {
				LOG.info("Has Basic credentials token");
				String[] creds = extractUsernamePassword(request);
				Credentials credentials = credentialsStore.authenticateByCredentials(creds[0], creds[1]);
				AuthenticationContext.get().setCredentials(credentials);
			} else {
				throw new AuthenticationException("No credentials provided!");
			}
		} catch (AuthenticationException e) {
			AuthenticationContext.get().setException(e);
		}
		chain.doFilter(request, response);
	}

	private boolean hasOAuthCredentials(ServletRequest request) {
		String token = extractToken(request);
		return token != null;
	}

	private boolean hasBasicCredentials(ServletRequest request) {
		String header = ((HttpServletRequest) request).getHeader("Authorization");
		return header != null;
	}

	private String extractToken(ServletRequest request) {
		return request.getParameter("token");
	}

	private String[] extractUsernamePassword(ServletRequest request) {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String header = httpServletRequest.getHeader("Authorization");
		if(!header.startsWith("Basic")){
			throw new RuntimeException("Authorization header is not formatted correctly");
		}
		String cipherText = header.split(" ")[1];
		String clearText = new String(Base64.decodeBase64(cipherText));
		return clearText.split(":");
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
