package com.square.engineering.rest;

public class APIException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private int httpStatusCode;
	

	public APIException(int httpStatusCode, String message){
		super(message);
		this.httpStatusCode = httpStatusCode;
	}


	public int getHttpStatusCode() {
		return httpStatusCode;
	}
	
}
