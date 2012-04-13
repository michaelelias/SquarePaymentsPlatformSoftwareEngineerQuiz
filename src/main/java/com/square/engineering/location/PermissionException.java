package com.square.engineering.location;

public class PermissionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PermissionException(String locationId, String ownerId){
		super("Merchant[id=" + ownerId + "] is not the owner of location[id=" + locationId + "]");
	}
	
}
