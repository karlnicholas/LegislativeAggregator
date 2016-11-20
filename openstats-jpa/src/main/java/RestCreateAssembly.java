

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import openstats.client.les.*;
import openstats.client.openstates.*;
import openstats.model.*;

public class RestCreateAssembly {

	public static void main(String[] args) throws Exception {
		new RestCreateAssembly().run();
	}
	
	public void run() throws Exception {
		ComputeAssembly2 computeAssembly = new ComputeAssembly2(); 
		
		Client client = ClientBuilder.newClient();

		for ( OpenState openState: OpenStateClasses.getOpenStates() ) {
			Assembly assembly = new Assembly(); 
			computeAssembly.computeAssemblyLES(openState, assembly);
			WebTarget myResource = client.target("http://localhost:8080/openstats/rest");
			Invocation.Builder builder = myResource.request(MediaType.APPLICATION_JSON);
			Response response = null;
			try {
				response = builder.post(Entity.json(assembly), Response.class);
				if ( response.getStatusInfo() == Response.Status.CREATED )
					System.out.println(response.getLocation().toString());
				else
					System.out.println("Failure status: " + response.getStatus() + ":" + response.getEntity());
			} catch ( BadRequestException e ) {
				System.out.print("BadRequest : " + e.getMessage()+":");
				System.out.println(builder.head().getHeaderString("error"));
			} finally {
				if ( response != null ) response.close();
			}
		}
		client.close();
        
	}
	

/*

	
		ComputeAssembly computeAssembly = new ComputeAssembly(); 
		
		OSAssembly osAssembly = computeAssembly.computeAssemblyLES(new OpenStateClasses.NCOpenState());
//		ObjectMapper objectMapper = new ObjectMapper();
//		String json = objectMapper.writeValueAsString(osAssembly);
//		System.out.println(json);
		
		Client client = ClientBuilder.newClient();
		WebTarget myResource = client.target("http://localhost:8080/openstats/rest");
		Invocation.Builder builder = myResource.request(MediaType.APPLICATION_JSON);
		Response response = builder.post(Entity.json(osAssembly), Response.class);
		
		if (response.getStatus() != Status.CREATED.getStatusCode() ) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}
	 
//		URI location = response.getLocation();
//		System.out.println(location.toString());
	 

 		OpenState testAction = new GAOpenState();
 		Assembly assembly = buildAssembly(testAction, em);
		writeJpa(assembly);	
		

	private static void printAllActions(Bill bill) {
		for ( Bill.Action action: bill.actions ) {
			System.out.println(action);
		}
	}
	private static TreeMap<org.openstates.data.Legislator, AuthorStats> readLegislators() throws Exception {
		TreeMap<org.openstates.data.Legislator, AuthorStats> legislators = new TreeMap<>();
		for ( org.openstates.data.Legislator legislator: org.openstates.model.Legislators.values()) {
			legislators.put(legislator, new AuthorStats());
		}
		return legislators;
	}
*/
	
}
