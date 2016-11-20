

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import openstats.client.les.Labels;
import openstats.client.openstates.*;

public class RestDeleteAssembly {

	public static void main(String[] args) throws Exception {
		new RestDeleteAssembly().run();
	}
	
	public void run() throws Exception {

//		OpenState openState = new OpenStateClasses.NCOpenState();
		
		Client client = ClientBuilder.newClient();
		
		for ( OpenState openState: OpenStateClasses.getOpenStates() ) {
			
			WebTarget myResource = client.target("http://localhost:8080/openstats/rest")
					.path("/{group}/{state}/{session}")
					.resolveTemplate("group", Labels.LESGROUPNAME)
					.resolveTemplate("state", openState.getState())
					.resolveTemplate("session", openState.getSession());
	
			Invocation.Builder builder = myResource.request(MediaType.APPLICATION_JSON);
			try {
				builder.delete();
				System.out.println(myResource.getUri().toString());
			} catch ( BadRequestException e ) {
				System.out.print("BadRequest : " + e.getMessage()+":");
				System.out.println(builder.head().getHeaderString("error"));
			}
		}
		client.close();
	}

}
