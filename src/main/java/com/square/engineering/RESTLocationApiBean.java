package com.square.engineering;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.square.engineering.security.AuthenticationContext;

@Path("locations")
public class RESTLocationApiBean {
	
	private static Logger LOG = Logger.getLogger(RESTLocationApiBean.class);
	
	private LocationRepository locationRepo = LocationRepository.get();
	
	@GET
	@Produces("application/json")
	@Path("/{locationId}")
	public Location getLocation(@PathParam("locationId") String locationId) throws APIException{
		checkAuthentication();
		LOG.info("Get location [id=" + locationId + "]");
		return locationRepo.get(locationId);
	}
	
	@PUT
	@Consumes("application/json")
	@Produces("application/json")
	@Path("/{locationId}")
	public Location updateLocation(@PathParam("locationId") String locationId, Location location) throws APIException{
		checkAuthentication();	
		LOG.info("Update location [id=" + locationId + ", " + location.toString() + "]");
		locationRepo.update(location);
		return location;
	}
	
	@DELETE
	@Path("/{locationId}")
	public void delete(@PathParam("locationId") String locationId) throws APIException{
		checkAuthentication();
		LOG.info("Delete location [id=" + locationId + "]");
		locationRepo.delete(locationId);
	}
	
	private void checkAuthentication() throws APIException {
		if(!AuthenticationContext.get().isAuthenticated()){
			throw new APIException(Status.UNAUTHORIZED.getStatusCode(), AuthenticationContext.get().getException().getMessage());
		}
	}
}
