package com.square.engineering.client;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.auth.BasicScheme;

public class BasicAuthenticationStrategy implements AuthenticationStrategy {

	private String username;
	private String password;

	public BasicAuthenticationStrategy(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public void applyCredentials(HttpClient client, HttpRequestBase request) {
		request.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username, password), "UTF-8", false));
	}

}
