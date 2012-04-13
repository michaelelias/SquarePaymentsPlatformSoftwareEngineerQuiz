package com.square.engineering.client;

import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

public class OAuthAuthenticationStrategy implements AuthenticationStrategy {

	private String token;

	public OAuthAuthenticationStrategy(String token) {
		this.token = token;
	}

	@Override
	public void applyCredentials(HttpClient client, HttpRequestBase request) {
		String query = request.getURI().getQuery();
		if(query == null){
			URI uri = request.getURI();
			uri = URI.create(uri.toString() + "?token=" + token);
			request.setURI(uri);
		} else {
			request.getURI().getQuery().concat("&token=" + token);
		}
		
	}
}
