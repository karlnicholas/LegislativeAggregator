

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import openstats.client.les.*;
import openstats.client.openstates.*;
import openstats.model.*;

public class RestUpdateAssembly {

	public static void main(String[] args) throws Exception {
		new RestUpdateAssembly().run();
	}
	
	public void run() throws Exception {
		
		ComputeAssembly2 computeAssembly = new ComputeAssembly2(); 
		
		Client client = ClientBuilder.newClient();
		
		for ( OpenState openState: OpenStateClasses.getOpenStates() ) {
			Assembly assembly = new Assembly();
			computeAssembly.computeAssemblyLES(openState, assembly);
		
			WebTarget myResource = client.target("http://localhost:8080/openstats/rest");
			Invocation.Builder builder = myResource.request(MediaType.APPLICATION_JSON);
			Response response=null;
			try {
				response = builder.put(Entity.json(assembly));
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
