package com.square.engineering.client;

import org.apache.http.client.methods.HttpRequestBase;

public class NoAuthenticationStrategy implements AuthenticationStrategy {

	@Override
	public void applyCredentials(HttpRequestBase request) {

	}

}
