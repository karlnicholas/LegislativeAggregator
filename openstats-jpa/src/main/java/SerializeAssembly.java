

import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.bind.*;

import openstats.client.les.ComputeAssembly2;
import openstats.client.openstates.OpenState;
import openstats.client.openstates.OpenStateClasses;
import openstats.model.Assembly;


public class SerializeAssembly {
	
	public static void main(String... args) throws Exception {
		new SerializeAssembly().run();		
	}
	
	private void run() throws Exception {
		OpenState testAction = new OpenStateClasses.GAOpenState();
		Assembly assembly = new Assembly();
		new ComputeAssembly2().computeAssemblyLES(testAction, assembly);
		JAXBContext ctx = JAXBContext.newInstance(Assembly.class);
		Marshaller marshaller = (Marshaller) ctx.createMarshaller();
		((javax.xml.bind.Marshaller) marshaller).marshal(assembly, Files.newOutputStream(Paths.get("c:/users/karl/GA-2013.XML")));
		System.out.flush();
		
	}
}
