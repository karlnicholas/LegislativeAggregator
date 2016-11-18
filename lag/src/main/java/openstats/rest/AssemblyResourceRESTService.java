/*
 * JBs, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package openstats.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import openstats.data.AssemblyRepository;
import openstats.dbmodel.*;
import openstats.facades.AssemblyFacade;
import openstats.model.Assembly;

/**
 * JAX-RS Example
 * <p/>
 * This class produces a RESTful service to read/write the contents of the assemblies table.
 */
@Path("")
@RequestScoped
public class AssemblyResourceRESTService {
	
	@Inject
	private Logger log;

    @Inject
    private AssemblyFacade assemblyFacade;
    
    @Inject
    private AssemblyRepository assemblyRepo;
    
    @POST
    @Consumes(value={MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createAssembly(@Context UriInfo ui, Assembly Assembly ) {

        Response.ResponseBuilder builder = null;
        
        try {
        	assemblyFacade.writeAssembly(Assembly);
        	
            // Create an "created" response
            builder = Response.created(ui.getRequestUriBuilder().path(
        			Assembly.getGroup().getGroupName() + "/" +
        			Assembly.getState() + "/" +
        			Assembly.getSession()
        		).build()
        	);
        } catch (Exception e) {
            // Handle generic exceptions
        	System.out.println("Bad Request:" + e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).header("error", e.getMessage());
        }

        return builder.build();
    }

//     @Produces(value={MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})

    @GET
    @Path("{group}/{state}/{session}")
    @Produces(value={MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response readAssembly(
    	@Context HttpHeaders httpHeaders, 
		@PathParam("group") String group,  
		@PathParam("state") String state, 
		@PathParam("session") String session
    ) throws OpenStatsException {
        Response.ResponseBuilder builder = null;
        try {
        	List<String> groupNames = new ArrayList<String>();
        	groupNames.add(group);
    		builder = Response.ok(assemblyRepo.buildAssemblyFromNames(groupNames, state, session), MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            // Handle generic exceptions
            builder = Response.status(Response.Status.BAD_REQUEST).header("error", e.getMessage());
        }
        return builder.build();
    }

    @GET
    @Path("/template/{state}/{session}")
    @Produces(value={MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getTemplateAssembly (
    	@Context HttpHeaders httpHeaders, 
		@PathParam("state") String state, 
		@PathParam("session") String session
    ) throws OpenStatsException {
        Response.ResponseBuilder builder = null;
        try {
    		builder = Response.ok(assemblyRepo.getAssembly(state, session), MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            // Handle generic exceptions
        	log.severe(e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).header("error", e.getMessage());
        }

        return builder.build();
    }

    @PUT
    @Consumes(value={MediaType.APPLICATION_JSON})
    public Response updateAssembly(Assembly assembly) {

        Response.ResponseBuilder builder = null;
        try {
        	assemblyFacade.writeAssembly(assembly);
            // Create an "created" response
            builder = Response.ok();
        } catch (Exception e) {
            // Handle generic exceptions
            builder = Response.status(Response.Status.BAD_REQUEST).header("error", e.getMessage());
        }
        return builder.build();
    }

    @DELETE
    @Path("{group}/{state}/{session}")
    public Response deleteAssembly(
		@PathParam("group") String group,  
		@PathParam("state") String state, 
		@PathParam("session") String session
    ) throws OpenStatsException {
        Response.ResponseBuilder builder = null;

        try {
            assemblyFacade.deleteAssemblyGroup(group, state, session);
            // Create an "ok" response
            builder = Response.ok();
        } catch (Exception e) {
            // Handle generic exceptions
            builder = Response.status(Response.Status.BAD_REQUEST).header("error", e.getMessage());
        }

        return builder.build();
    }

    /**
     * Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message. This can then be used
     * by clients to show violations.
     *
     * @param violations A set of violations that needs to be reported
     * @return JAX-RS response containing all violations
     */
/*    
    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<String, String>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().ttring(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }
*/
    /**
     * Checks if a assembly with the same email address is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "email")" constraint from the Assembly class.
     *
     * @param stateKey the state to check
     * @param assembleyKey the Assembly.assembly to check
     * @return True if the email already exists, and false otherwise
     */
/*    
    public boolean stateSessionAlreadyExists(String state, String session) {
        DBAssembly assembly = null;
        try {
            assembly = repository.findByStateSession(state, session);
        } catch (NoResultException e) {
            // ignore
        }
        return assembly != null;
    }
*/    
}
