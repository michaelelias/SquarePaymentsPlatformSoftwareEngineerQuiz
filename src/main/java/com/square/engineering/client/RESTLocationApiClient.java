package com.square.engineering.client;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.square.engineering.location.Location;
import com.square.engineering.rest.APIException;

public class RESTLocationApiClient {
	
	private static Logger LOG = Logger.getLogger(RESTLocationApiClient.class);

	private HttpClient httpClient;
	private String endpointUrl;
	private AuthenticationStrategy authStrategy;

	public RESTLocationApiClient(HttpClient httpClient, String serverUrl) {
		this.httpClient = httpClient;
		endpointUrl = serverUrl + "/rest/locations/";
	}
	
	public void setAuthStrategy(AuthenticationStrategy authStrategy) {
		this.authStrategy = authStrategy;
	}

	public Location get(String locationId) throws APIException{
		HttpGet get = new HttpGet(endpointUrl + locationId);
		return toLocation(execute(get));
	}
	
	public Location update(String locationId, Location location) throws APIException{
		HttpPut put = new HttpPut(endpointUrl + locationId);
		put.addHeader("Content-Type", "application/json");
		try{
		put.setEntity(new StringEntity(toJSON(location)));
		} catch(Exception e){
			throw new RuntimeException(e);
		}
		return toLocation(execute(put));
	}
	
	public void delete(String locationId) throws APIException{
		HttpDelete delete = new HttpDelete(endpointUrl + locationId);
		execute(delete);
	}
	
	private Location toLocation(HttpResponse response){
		try {
			String content = EntityUtils.toString(response.getEntity());
			return fromJSON(content);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private String toJSON(Location location){
		Gson gson = new Gson();
		return gson.toJson(location);
	}
	
	private Location fromJSON(String json){
		Gson gson = new Gson();
		return gson.fromJson(json, Location.class);
	}
	private HttpResponse execute(HttpRequestBase request) throws APIException {
		try {
			logRequest(request);
			authStrategy.applyCredentials(httpClient, request);
			HttpResponse response = httpClient.execute(request);
			logResponse(response);
			if(isStatusOK(response)){
				return response;
			}
			throw new APIException(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity()));
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private boolean isStatusOK(HttpResponse response){
		int statusCode = response.getStatusLine().getStatusCode(); 
		return statusCode >= 200 && statusCode < 300;
	}

	private void logRequest(HttpUriRequest httpRequest) {
		String bodyContent = null;
		if (httpRequest instanceof HttpEntityEnclosingRequestBase) {
			try {
				HttpEntity entity = ((HttpEntityEnclosingRequestBase) httpRequest).getEntity();
				if (entity != null) {
					InputStream is = entity.getContent();
					bodyContent = IOUtils.toString(is);
				}
			} catch (Exception e) {
				LOG.debug("Failed to read request body : " + e.getMessage(), e);
			}
		}
		LOG.info("===>>> " + httpRequest.getMethod() + " " + httpRequest.getURI() + (bodyContent != null ? " - CONTENT[" + bodyContent + "]" : " - NO CONTENT"));
	}

	private void logResponse(HttpResponse httpResponse) {
		if (httpResponse != null) {
			String bodyContent = null;
			if (httpResponse.getEntity() != null) {
				try {
					httpResponse.setEntity(new BufferedHttpEntity(httpResponse.getEntity()));
					bodyContent = IOUtils.toString(httpResponse.getEntity().getContent());
				} catch (Exception e) {
					LOG.debug("Failed to read response body : " + e.getMessage(), e);
				}
			}
			LOG.info("<<<=== " + httpResponse.getStatusLine().getStatusCode() + (bodyContent != null ? " - CONTENT[" + bodyContent + "]" : " - NO CONTENT"));
		} else {
			LOG.info("<<<=== NULL Response");
		}
	}
}
