package com.square.engineering.client;

import com.google.gson.Gson;
import com.square.engineering.location.Location;
import com.square.engineering.rest.APIException;

public class TrafficGenerator {

	private RESTLocationApiClient client;
	
	private String[] locations = {"123", "456", "321", "654"};

	public TrafficGenerator(RESTLocationApiClient client) {
		this.client = client;
	}

	/**
	 * 
	 * 
	 * @param duration
	 * @param interval
	 */
	public void generate(int duration, int interval) {
		long endTime = System.currentTimeMillis() + duration;
		while (System.currentTimeMillis() < endTime) {
			try {
				generateApiCall().execute();
			} catch (APIException e) {
				// ignore
			}
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

	}

	private ApiCall generateApiCall() {
		return new ApiCall(ApiCallType.pickRandom(), Authorizations.pickRandom(), pickRandomLocation());
	}
	
	private String pickRandomLocation(){
		int random = (int)(Math.random() * locations.length);
		return locations[random];
	}

	private enum ApiCallType {
		GET, PUT, DELETE;
		
		public static ApiCallType pickRandom(){
			int random = (int)(Math.random() * 4);
			if(random == 1){
				return GET;
			} else if(random == 2){
				return PUT;
			} else if(random == 3){
				return DELETE;
			} 
			return GET;
		}
	}

	private class ApiCall {

		private ApiCallType type;
		private AuthenticationStrategy authStrat;
		private String locationId;
		private String content;

		public ApiCall(ApiCallType type, AuthenticationStrategy authStrat, String locationId, String content) {
			super();
			this.type = type;
			this.authStrat = authStrat;
			this.locationId = locationId;
			this.content = content;
		}

		public ApiCall(ApiCallType type, AuthenticationStrategy authStrat, String locationId) {
			super();
			this.type = type;
			this.authStrat = authStrat;
			this.locationId = locationId;
		}

		public void execute() throws APIException {
			client.setAuthStrategy(authStrat);
			switch (type) {
			case GET:
				client.get(locationId);
				break;
			case PUT:
				Gson gson = new Gson();
				client.update(locationId, gson.fromJson(content, Location.class));
				break;
			case DELETE:
				client.delete(locationId);
				break;
			}
		}

	}

}
