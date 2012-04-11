package com.square.engineering.client;


import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicHeader;

public class BasicAuthenticationStrategy implements AuthenticationStrategy {

	private String username;
	private String password;
	
	public BasicAuthenticationStrategy(String username, String password){
		this.username = username;
		this.password = password;
	}
	
	@Override
	public void applyCredentials(HttpRequestBase request) {
		Header header = new BasicHeader("Authorization", getAuthorizationHeaderValue());
		request.addHeader(header);
	}
	
	private String getAuthorizationHeaderValue(){
		Base64 base64 = new Base64();
		String clearText = username + ":" + password;
		return "Basic " + new String(base64.encode(clearText.getBytes()));
	}

}
