

import java.nio.file.*;

import org.codehaus.jackson.map.ObjectMapper;

import openstats.client.les.*;
import openstats.client.openstates.OpenState;
import openstats.client.openstates.OpenStateClasses;
import openstats.client.rest.RESTClient;
import openstats.model.Assembly;


public class SerializeLESAssemblies {
	
	public static void main(String... args) throws Exception {
		new SerializeLESAssemblies().run();		
	}
	
	private void run() throws Exception {
		ComputeAssembly2 computeAssembly = new ComputeAssembly2();
		RESTClient restClient = new RESTClient();

		ObjectMapper mapper = new ObjectMapper();

		for( OpenState openState: OpenStateClasses.getOpenStates() ) {
//			OpenState openState = new OpenStateClasses.CAOpenState();
			Assembly templateAssembly = restClient.getTemplateAssembly(openState.getState(), openState.getSession());
			Assembly assembly = new Assembly(templateAssembly);
			computeAssembly.computeAssemblyLES(openState, assembly);
			restClient.updateAssembly(assembly);
			Path path = Paths.get("c:/users/karl/workspace/openstats-jpa/results/"+openState.getState()+"-"+openState.getSession()+" Results.json");
			
//			mapper.writeValue(path.toFile(), assembly);
			mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), assembly);
		}
	}
}
