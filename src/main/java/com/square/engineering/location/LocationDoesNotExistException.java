package com.square.engineering.location;

public class LocationDoesNotExistException extends RuntimeException {

	public LocationDoesNotExistException(String locationId){
		super("Location with id " + locationId + " does not exist");
	}
	
}
