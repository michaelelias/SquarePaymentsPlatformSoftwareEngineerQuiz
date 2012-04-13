package com.square.engineering.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.square.engineering.location.Location;
import com.square.engineering.location.LocationDoesNotExistException;
import com.square.engineering.location.LocationRepository;
import com.square.engineering.location.PermissionException;
import com.square.engineering.security.AuthenticationContext;

@Path("locations")
public class RESTLocationApiBean {

	private static Logger LOG = Logger.getLogger(RESTLocationApiBean.class);

	private LocationRepository locationRepo = LocationRepository.get();

	@GET
	@Produces("application/json")
	@Path("/{locationId}")
	public Location getLocation(@PathParam("locationId") String locationId) throws APIException {
		checkAuthentication();
		LOG.info("Get location [id=" + locationId + "]");
		try {
			return locationRepo.get(locationId, getAuthorizedMerchantId());
		} catch (Exception e) {
			throw convertException(e);
		}
	}

	@PUT
	@Consumes("application/json")
	@Produces("application/json")
	@Path("/{locationId}")
	public Location updateLocation(@PathParam("locationId") String locationId, Location locationUpdate) throws APIException {
		checkAuthentication();
		LOG.info("Update location [id=" + locationId + ", " + locationUpdate.toString() + "]");
		try {
			return locationRepo.update(locationId, locationUpdate, getAuthorizedMerchantId());
		} catch (Exception e) {
			throw convertException(e);
		}
	}

	@DELETE
	@Path("/{locationId}")
	public void delete(@PathParam("locationId") String locationId) throws APIException {
		checkAuthentication();
		LOG.info("Delete location [id=" + locationId + "]");
		try {
			locationRepo.delete(locationId, getAuthorizedMerchantId());
		} catch (Exception e) {
			throw convertException(e);
		}
	}

	private void checkAuthentication() throws APIException {
		if (!AuthenticationContext.get().isAuthenticated()) {
			throw new APIException(Status.UNAUTHORIZED.getStatusCode(), AuthenticationContext.get().getException().getMessage());
		}
	}

	private String getAuthorizedMerchantId() {
		return AuthenticationContext.get().getCredentials().getId();
	}

	private APIException convertException(Exception e) {
		APIException apiException;
		if (e instanceof PermissionException) {
			apiException = new APIException(Status.FORBIDDEN.getStatusCode(), e.getMessage());
		} else if (e instanceof LocationDoesNotExistException) {
			apiException = new APIException(Status.NOT_FOUND.getStatusCode(), e.getMessage());
		} else {
			apiException = new APIException(Status.INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage());
		}
		return apiException;
	}
}
