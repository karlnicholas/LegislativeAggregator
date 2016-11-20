package openstats.client.rest;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;

import openstats.model.Assembly;

public class RESTClient {
	

	public Assembly getTemplateAssembly(String state, String session) {
		Client client = ClientBuilder.newClient();
		WebTarget myResource = client.target("http://localhost:8080/openstats/rest")
			.path("/template/{state}/{session}")
			.resolveTemplate("state", state)
			.resolveTemplate("session", session);
		Invocation.Builder builder = myResource.request(MediaType.APPLICATION_JSON);
		try {
			return builder.get(Assembly.class);
		} catch ( BadRequestException e ) {
			throw new RuntimeException(builder.head().getHeaderString("error"), e);
		} finally {
			client.close();
		}
	}
	
	public void updateAssembly(Assembly assembly) {
		Client client = ClientBuilder.newClient();
		WebTarget myResource = client.target("http://localhost:8080/openstats/rest");
		Invocation.Builder builder = myResource.request(MediaType.APPLICATION_JSON);
		try {
			Response response = builder.put(Entity.json(assembly));
			if ( response.getStatus() != Status.OK.getStatusCode() ) {
				throw new RuntimeException(response.getHeaderString("error"));
			}
		} catch ( BadRequestException e ) {
			throw new RuntimeException(e);
		} finally {
			client.close();
		}

	}
	
}
