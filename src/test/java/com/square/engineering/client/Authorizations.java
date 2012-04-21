package com.square.engineering.client;


public class Authorizations {

	public static final AuthenticationStrategy noAuthenticationStrategy = new NoAuthenticationStrategy();
	public static final AuthenticationStrategy traderJoesOAuthStrategy = new OAuthAuthenticationStrategy("123456789");
	public static final AuthenticationStrategy traderJoesBasicStrategy = new BasicAuthenticationStrategy("joe", "trader");
	public static final AuthenticationStrategy moonbucksOAuthStrategy = new OAuthAuthenticationStrategy("987654321");
	public static final AuthenticationStrategy moonbucksBasicStrategy = new BasicAuthenticationStrategy("moon", "bucks");
	
	public static AuthenticationStrategy pickRandom(){
		int random = (int)(Math.random() * 6);
		if(random == 1){
			return noAuthenticationStrategy;
		} else if(random == 2){
			return traderJoesOAuthStrategy;
		} else if(random == 3){
			return traderJoesBasicStrategy;
		} else if(random == 4){
			return moonbucksOAuthStrategy;
		} else if(random == 5){
			return moonbucksBasicStrategy;
		}
		return noAuthenticationStrategy;
	}
	
}
