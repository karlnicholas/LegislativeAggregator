import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;

import openstats.client.les.ComputeAssembly2;
import openstats.client.openstates.OpenState;
import openstats.client.openstates.OpenStateClasses;
import openstats.model.Assembly;


public class DeserializeAssembly {
	
	public static void main(String... args) throws Exception {
		new DeserializeAssembly().run();		
	}
	
	private void run() throws Exception {
		OpenState testAction = new OpenStateClasses.CAOpenState();
		Assembly assembly = new Assembly();  
		new ComputeAssembly2().computeAssemblyLES(testAction, assembly);
		JAXBContext ctx = JAXBContext.newInstance(Assembly.class);
		ctx.createMarshaller().marshal(assembly, Files.newOutputStream(Paths.get("c:/users/karl/CA-2013.xml")));
		
	}

}
