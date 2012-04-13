package com.square.engineering.rest;

import java.lang.reflect.Type;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.core.header.OutBoundHeaders;
import com.sun.jersey.core.spi.factory.ResponseImpl;

@Provider
public class APIExceptionMapper implements ExceptionMapper<APIException> {

	@Override
	public Response toResponse(final APIException exception) {
        OutBoundHeaders headers = new OutBoundHeaders();
        headers.add("Content-Type", "application/text; charset=UTF-8");
		return new ExceptionResponse(exception.getHttpStatusCode(), headers, exception.getMessage(), String.class);
	}

	private class ExceptionResponse extends ResponseImpl {

		protected ExceptionResponse(int status, OutBoundHeaders headers, Object entity, Type entityType) {
			super(status, headers, entity, entityType);
		}
		
	}
	
}
