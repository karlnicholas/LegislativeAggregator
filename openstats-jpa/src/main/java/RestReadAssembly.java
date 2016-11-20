
import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import openstats.client.les.Labels;
import openstats.client.openstates.*;
import openstats.model.Assembly;

public class RestReadAssembly {

	public static void main(String[] args) throws Exception {
		new RestReadAssembly().run();
	}
	
	public void run() throws Exception {
        
		Client client = ClientBuilder.newClient();
		
		for ( OpenState openState: OpenStateClasses.getOpenStates() ) {
		
			WebTarget myResource = client.target("http://localhost:8080/openstats/rest")
					.path("/{group}/{state}/{session}")
					.resolveTemplate("group", Labels.LESGROUPNAME)
					.resolveTemplate("state", openState.getState())
					.resolveTemplate("session", openState.getSession());
	
			Invocation.Builder builder = myResource.request(MediaType.APPLICATION_JSON);
			try {
				Assembly assembly = builder.get(Assembly.class);
				System.out.println(assembly.getState()+"-"+assembly.getSession()+" " + assembly.getResults().get(0));
			} catch ( BadRequestException e ) {
				System.out.print("BadRequest : " + e.getMessage()+":");
				System.out.println(builder.head().getHeaderString("error"));
			}

		}
		client.close();

	}


/*

		
		OpenState openState = new OpenStateClasses.AROpenState();

		Client client = ClientBuilder.newClient();

		WebTarget myResource = client.target("http://localhost:8080/openstats/rest")
				.path("{group}/{state}/{session}")
				.resolveTemplate("group", Labels.LESGROUPNAME)
				.resolveTemplate("state", openState.getState())
				.resolveTemplate("session", openState.getSession());

		Invocation.Builder builder = myResource.request(MediaType.APPLICATION_JSON);
		OSAssembly assembly = builder.get(OSAssembly.class);
		Response response = builder.head();
		
		if (builder.head().getStatus() != Status.OK.getStatusCode() ) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}
	 
		System.out.println(assembly.toString());
		response.getLocation();
		
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
