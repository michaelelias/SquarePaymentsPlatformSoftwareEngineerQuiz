package com.square.engineering;

public class APIException extends Exception {

	private int httpStatusCode;
	

	public APIException(int httpStatusCode, String message){
		super(message);
		this.httpStatusCode = httpStatusCode;
	}


	public int getHttpStatusCode() {
		return httpStatusCode;
	}
	
}
