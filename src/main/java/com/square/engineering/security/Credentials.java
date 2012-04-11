package com.square.engineering.security;

public class Credentials {

	private String name;
	private String oauthToken;
	private String basicUsername;
	private String basicPassword;

	public Credentials(String name, String oauthToken, String basicUsername, String basicPassword) {
		super();
		this.name = name;
		this.oauthToken = oauthToken;
		this.basicUsername = basicUsername;
		this.basicPassword = basicPassword;
	}

	public String getName() {
		return name;
	}

	public String getOauthToken() {
		return oauthToken;
	}

	public String getBasicUsername() {
		return basicUsername;
	}

	public String getBasicPassword() {
		return basicPassword;
	}

	@Override
	public String toString() {
		return "Credentials [name=" + name + ", oauthToken=" + oauthToken + ", basicUsername=" + basicUsername + ", basicPassword=" + basicPassword + "]";
	}

}
